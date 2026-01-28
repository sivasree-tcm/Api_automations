package models.roles;

public class CreateRolesRequest {

    private String refProjectId;
    private String roleName;
    private String roleDescription;
    private Integer userId;

    public String getRefProjectId() {
        return refProjectId;
    }

    public void setRefProjectId(String refProjectId) {
        this.refProjectId = refProjectId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
