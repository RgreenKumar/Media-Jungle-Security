# Media Jungle - Database & Media Backup and Recovery Plan
**SOC 2 Compliance Reference**: Control 18 — Backup and Recovery  
**Target Database**: PostgreSQL 18  
**Target Storage**: Media & File Upload Directories (`uploads/`, `storage/`)

---

## 1. Overview
This document specifies the comprehensive data backup, disaster recovery, and data integrity verification procedures for the Media Jungle streaming platform. In accordance with SOC 2 Trust Services Criteria (Availability & Confidentiality), all customer records, audit logs, transactions, and uploaded media assets are protected with automated backup strategies and documented restore procedures.

---

## 2. PostgreSQL Database Backup Strategy

### 2.1 Backup Cadence & Retention
| Backup Type | Frequency | Retention Window | Storage Location |
| :--- | :--- | :--- | :--- |
| **Full Database Dump** (`pg_dump`) | Nightly at 01:00 UTC | 30 Days (Rolling) | Encrypted Secondary Storage / S3 |
| **Write-Ahead Logs (WAL)** | Continuous / Hourly | 7 Days | WAL Archive Directory |
| **Monthly Snapshot** | 1st of every month | 12 Months | Long-Term Archive (Glacier / Cold Storage) |

### 2.2 Automated PostgreSQL Backup Script
Save as `scripts/db_backup.ps1` (Windows) or `scripts/db_backup.sh` (Linux):

```powershell
# Windows PowerShell Automated PostgreSQL Backup
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupDir = "C:\Backups\MediaJungle\db"
$BackupFile = "$BackupDir\mediajungle_backup_$Timestamp.sql.gz"
$LogFile = "$BackupDir\backup_log.txt"

If (!(Test-Path -Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force
}

$env:PGPASSWORD = $env:DB_PASSWORD
$pgDump = "C:\Program Files\PostgreSQL\18\bin\pg_dump.exe"

# Execute compressed full backup
& $pgDump -U postgres -h localhost -p 5432 -F c -b -v -f $BackupFile mediaJungle 2>&1 | Tee-Object -FilePath $LogFile

# Retain only last 30 days of backups
Get-ChildItem -Path $BackupDir -Filter "*.sql.gz" | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-30) } | Remove-Item -Force
```

---

## 3. Uploaded Media Assets Backup Strategy

### 3.1 Media Folders Covered
- Video streaming content: `uploads/videos`, `uploads/vr_videos`, `uploads/trailers`
- Audio tracks: `uploads/audio`
- User avatars & metadata: `uploads/profiles`, `uploads/licences`
- Private secure documents: `storage/private`

### 3.2 Media Sync Command
Media files are synchronized daily using block-level differential copies:
```powershell
# Differential sync to secondary/cloud backup location
robocopy "C:\Users\RH\Downloads\Media-Jungle-Security-main-Final\uploads" "D:\MediaJungle_Media_Backup" /E /ZB /COPY:DAT /R:3 /W:5 /LOG+:"C:\Backups\media_sync.log"
```

---

## 4. Disaster Recovery Procedure

### 4.1 Prerequisites for Restore
1. PostgreSQL 18 service running on target host.
2. Target database created with appropriate permissions:
   ```sql
   CREATE DATABASE mediaJungle;
   ```
3. Most recent valid backup file (`.sql.gz` or `.dump`).

### 4.2 Database Restoration Steps
1. **Stop Application Backend**:
   Prevent concurrent write operations during restore:
   ```powershell
   Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
   ```
2. **Execute Restore**:
   ```powershell
   $env:PGPASSWORD = "your_db_password"
   & "C:\Program Files\PostgreSQL\18\bin\pg_restore.exe" -U postgres -h localhost -p 5432 -d mediaJungle --clean --if-exists -v "C:\Backups\MediaJungle\db\mediajungle_backup_latest.sql.gz"
   ```
3. **Run Schema Verification**:
   Verify key tables exist and row counts match expectations:
   ```sql
   SELECT table_name, (xpath('/row/cnt/text()', xml_count))[1]::text::int as count
   FROM (
     SELECT table_name, table_schema,
            query_to_xml(format('select count(*) as cnt from %I.%I', table_schema, table_name), false, true, '') as xml_count
     FROM information_schema.tables
     WHERE table_schema = 'public' AND table_name IN ('user_register', 'add_user', 'audit_log')
   ) t;
   ```
4. **Restart Backend Service**:
   ```powershell
   mvn spring-boot:run
   ```

---

## 5. Restore Testing & Verification Schedule
- **Testing Frequency**: Quarterly disaster recovery fire drill.
- **Success Criteria**:
  - Recovery Time Objective (RTO): $\le 2$ hours.
  - Recovery Point Objective (RPO): $\le 1$ hour.
  - Full application integrity verification: admin login, user video playback, audit log verification.
