// ===========================================
// Internship Security Enhancement
// Feature : Security Standards Registry
// Standards : ISO27001 & SOC 2 Compliance
// ===========================================
package com.VsmartEngine.MediaJungle.security.standards;

public class SecurityStandard {

    private Long id;
    private String framework;
    private String code;
    private String name;
    private String domain;
    private String description;
    private String implementedComponent;
    private String status;

    public SecurityStandard() {
    }

    public SecurityStandard(Long id, String framework, String code, String name, String domain,
                            String description, String implementedComponent, String status) {
        this.id = id;
        this.framework = framework;
        this.code = code;
        this.name = name;
        this.domain = domain;
        this.description = description;
        this.implementedComponent = implementedComponent;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImplementedComponent() {
        return implementedComponent;
    }

    public void setImplementedComponent(String implementedComponent) {
        this.implementedComponent = implementedComponent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
