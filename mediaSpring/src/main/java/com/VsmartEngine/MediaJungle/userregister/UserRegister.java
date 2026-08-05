package com.VsmartEngine.MediaJungle.userregister;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
	
	@Column(name="password")
	private String password;
	
	@Column(name="mobnum")
	private  String mobnum;
	
	@Column(name="date")
	private LocalDate date ;
	
	@Lob
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

	// GDPR-TASK-01: Explicit consent flag captured at registration (GDPR Art. 6 & 7 - lawful basis / consent).
	// Registration is rejected server-side (see UserRegisterController#register) if this is not true.
	@Column(name = "consent_given")
	private boolean consentGiven = false;

	// GDPR-TASK-02: Timestamp of when consent was given, required to prove consent was obtained (GDPR Art. 7(1)).
	@Column(name = "consent_timestamp")
	private LocalDateTime consentTimestamp;

	// GDPR-TASK-03: Separate, granular opt-in for marketing communications, independent of the mandatory
	// account-terms consent above (GDPR Art. 4(11) - consent must be "specific" and "unbundled").
	@Column(name = "marketing_opt_in")
	private boolean marketingOptIn = false;

	// GDPR-TASK-04: Version of the Privacy Policy / Terms the user agreed to, so we can detect when a
	// user needs to re-consent after a policy change (GDPR Art. 7(1) - accountability for consent).
	@Column(name = "terms_version")
	private String termsVersion;

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

	// --- GDPR consent accessors (see GDPR-TASK-01..04 above) ---
	public boolean isConsentGiven() {
		return consentGiven;
	}

	public void setConsentGiven(boolean consentGiven) {
		this.consentGiven = consentGiven;
	}

	public LocalDateTime getConsentTimestamp() {
		return consentTimestamp;
	}

	public void setConsentTimestamp(LocalDateTime consentTimestamp) {
		this.consentTimestamp = consentTimestamp;
	}

	public boolean isMarketingOptIn() {
		return marketingOptIn;
	}

	public void setMarketingOptIn(boolean marketingOptIn) {
		this.marketingOptIn = marketingOptIn;
	}

	public String getTermsVersion() {
		return termsVersion;
	}

	public void setTermsVersion(String termsVersion) {
		this.termsVersion = termsVersion;
	}
}
