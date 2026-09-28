package com.VsmartEngine.MediaJungle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
<<<<<<< HEAD
=======
import com.fasterxml.jackson.annotation.JsonProperty;
>>>>>>> internship/main

@Entity
@Table
public class AddUser {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="username")
	private String Username;
	
	@Column(unique = true)
	private String email;
	
	@Column(name="role")
	private String role;
	
	@Column(name="mobnum")
	private  String mobnum;
	
	@Column(name="compname")
	private String compname;
	
	@Column(name="pincode")
	private String pincode;
	
	@Column(name="country")
	private String country;
	
<<<<<<< HEAD
	@Column(name="password")
	private String password;
	
=======
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name="password")
	private String password;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
>>>>>>> internship/main
	@Column(name="confirm_Password")
	private String confirmPassword;
	
	@Column(name="address")
	private String address;

<<<<<<< HEAD
=======
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

>>>>>>> internship/main
	public AddUser() {
		super();
	}

	public AddUser(long id, String username, String email, String role, String mobnum, String compname, String pincode,
			String country, String password, String confirmPassword, String address) {
		super();
		this.id = id;
		Username = username;
		this.email = email;
		this.role = role;
		this.mobnum = mobnum;
		this.compname = compname;
		this.pincode = pincode;
		this.country = country;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.address = address;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobnum() {
		return mobnum;
	}

	public void setMobnum(String mobnum) {
		this.mobnum = mobnum;
	}

	public String getCompname() {
		return compname;
	}

	public void setCompname(String compname) {
		this.compname = compname;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
<<<<<<< HEAD
	}	
=======
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
>>>>>>> internship/main
}
