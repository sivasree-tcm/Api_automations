package utils;

public class CredentialStore {

    private static Integer credentialId;

    public static void setCredentialId(Integer id) {
        credentialId = id;
    }

    public static Integer getCredentialId() {
        if (credentialId == null) {
            throw new IllegalStateException(
                    "❌ Credential ID not set"
            );
        }
        return credentialId;
    }
}
