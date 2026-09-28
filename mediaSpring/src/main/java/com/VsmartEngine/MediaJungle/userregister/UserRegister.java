package com.VsmartEngine.MediaJungle.userregister;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.VsmartEngine.MediaJungle.model.PaymentUser;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class UserRegister {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="username")
	private String username;
	
	@Column(unique = true)
	private String email;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name="password")
	private String password;
	
	@Column(name="mobnum")
	private  String mobnum;
	
	@Column(name="date")
	private LocalDate date ;
	
	@Column(name="profile" ,length=1000000)
	private byte[] profile;
	
	@ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite_audios", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "audio_id")
    private Set<Long> favoriteAudioIds = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_favorite_videos", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "video_id")
    private Set<Long> favoriteVideosIds = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "watch_later_videos", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "video_id")
    private Set<Long> watchlaterIds = new HashSet<>();
	
	@OneToOne
	@JoinColumn(name = "Payment_details")
	private PaymentUser paymentId;

	// =======================================
	// Internship Security Enhancement
	// Feature: Role Based Access Control
	// ISO27001 Control: Access Control
	// =======================================
	// RBAC: Default user role set to USER, column non-nullable
	@Column(name = "role", nullable = false)
	private String role = com.VsmartEngine.MediaJungle.security.UserRole.USER.name();

	// ========================================
	// Internship Security Enhancement
	// Feature : Brute Force Protection
	// ISO27001 Control : Secure Authentication
	// ========================================
	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "failed_login_attempts")
	private Integer failedLoginAttempts = 0;

	@Column(name = "account_locked")
	private Boolean accountLocked = false;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "account_locked_until")
	private java.time.LocalDateTime accountLockedUntil;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "last_failed_login")
	private java.time.LocalDateTime lastFailedLogin;

	// ========================================
	// SOC 2 Control 4: Multi-Factor Authentication
	// ========================================
	@Column(name = "mfa_enabled")
	private Boolean mfaEnabled = false;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "mfa_secret")
	private String mfaSecret;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "mfa_backup_codes", columnDefinition = "TEXT")
	private String mfaBackupCodes;

	// ========================================
	// SOC 2 Control 6: User Lifecycle Status
	// ========================================
	@Column(name = "status")
	private String status = "ACTIVE";
	
	public UserRegister() {
		super();
	}

	public UserRegister(long id, String username, String email, String password,
			String mobnum, LocalDate date, byte[] profile, Set<Long> favoriteAudioIds,
			Set<Long> favoriteVideosIds, Set<Long> watchlaterIds, PaymentUser paymentId) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.mobnum = mobnum;
		this.date = date;
		this.profile = profile;
		this.favoriteAudioIds = favoriteAudioIds;
		this.favoriteVideosIds = favoriteVideosIds;
		this.watchlaterIds = watchlaterIds;
		this.paymentId = paymentId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobnum() {
		return mobnum;
	}

	public void setMobnum(String mobnum) {
		this.mobnum = mobnum;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public byte[] getProfile() {
		return profile;
	}

	public void setProfile(byte[] profile) {
		this.profile = profile;
	}

	public Set<Long> getFavoriteAudioIds() {
		return favoriteAudioIds;
	}

	public void setFavoriteAudioIds(Set<Long> favoriteAudioIds) {
		this.favoriteAudioIds = favoriteAudioIds;
	}

	public Set<Long> getFavoriteVideosIds() {
		return favoriteVideosIds;
	}

	public void setFavoriteVideosIds(Set<Long> favoriteVideosIds) {
		this.favoriteVideosIds = favoriteVideosIds;
	}

	public PaymentUser getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(PaymentUser paymentId) {
		this.paymentId = paymentId;
	}

	public Set<Long> getWatchlaterIds() {
		return watchlaterIds;
	}

	public void setWatchlaterIds(Set<Long> watchlaterIds) {
		this.watchlaterIds = watchlaterIds;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts != null ? failedLoginAttempts : 0;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public boolean isAccountLocked() {
		return accountLocked != null && accountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		this.accountLocked = accountLocked;
	}

	public java.time.LocalDateTime getAccountLockedUntil() {
		return accountLockedUntil;
	}

	public void setAccountLockedUntil(java.time.LocalDateTime accountLockedUntil) {
		this.accountLockedUntil = accountLockedUntil;
	}

	public java.time.LocalDateTime getLastFailedLogin() {
		return lastFailedLogin;
	}

	public void setLastFailedLogin(java.time.LocalDateTime lastFailedLogin) {
		this.lastFailedLogin = lastFailedLogin;
	}

	public boolean isMfaEnabled() {
		return mfaEnabled != null && mfaEnabled;
	}

	public void setMfaEnabled(boolean mfaEnabled) {
		this.mfaEnabled = mfaEnabled;
	}

	public String getMfaSecret() {
		return mfaSecret;
	}

	public void setMfaSecret(String mfaSecret) {
		this.mfaSecret = mfaSecret;
	}

	public String getMfaBackupCodes() {
		return mfaBackupCodes;
	}

	public void setMfaBackupCodes(String mfaBackupCodes) {
		this.mfaBackupCodes = mfaBackupCodes;
	}

	public String getStatus() {
		return status != null ? status : "ACTIVE";
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
