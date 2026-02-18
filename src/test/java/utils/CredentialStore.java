package utils;

public class CredentialStore {
    private static Integer credentialId = 1; // 👈 Set default to 1

    public static void setCredentialId(Integer id) {
        credentialId = id;
    }

    public static Integer getCredentialId() {
        // Fallback to 1 if it's somehow null
        if (credentialId == null) {
            return 1;
        }
        return credentialId;
    }
}

