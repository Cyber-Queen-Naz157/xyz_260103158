public class Task5 {

    static class UserSession {
        private final String username;
        private final String role;

        public UserSession(String username, String role) {
            this.username = username;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }

    interface StorageDriver {
        byte[] loadRawBlob(String path);
        String getStorageId();
    }

    static class S3StorageDriver implements StorageDriver {
        @Override
        public byte[] loadRawBlob(String path) {
            System.out.println("FETCHING raw media from AWS S3");
            return new byte[1024];
        }

        @Override
        public String getStorageId() {
            return "AMAZON_S3_CLOUD";
        }
    }

    static class NfsStorageDriver implements StorageDriver {
        @Override
        public byte[] loadRawBlob(String path) {
            System.out.println("FETCHING raw media from Local NFS");
            return new byte[2048];
        }

        @Override
        public String getStorageId() {
            return "LOCAL_NFS_CLUSTER";
        }
    }

    static class StorageDriverProxy implements StorageDriver {
        private final StorageDriver realDriver;
        private final UserSession session;

        public StorageDriverProxy(StorageDriver driver, UserSession session) {
            if (driver == null || session == null)
                throw new IllegalArgumentException("Dependencies cannot be null");

            this.realDriver = driver;
            this.session = session;
        }

        @Override
        public byte[] loadRawBlob(String resourcePath) {
            System.out.println("SYSTEM: Security Access Control Check for User '"
                    + session.getUsername() + "'");

            if (session.getRole().equals("GUEST")) {
                System.out.println(
                        "SECURITY ALERT: [PROXY BLOCKED] Guest role cannot access backend.");
                return new byte[0];
            }

            System.out.println("SYSTEM: PROXY AUDIT Authorized user: "
                    + session.getUsername());

            return realDriver.loadRawBlob(resourcePath);
        }

        @Override
        public String getStorageId() {
            return "PROXY_GUARDED::" + realDriver.getStorageId();
        }
    }

    static abstract class DigitalAsset {
        protected final StorageDriver driver;
        protected final String assetPath;

        public DigitalAsset(StorageDriver driver, String assetPath) {
            if (driver == null)
                throw new IllegalArgumentException("Driver cannot be null");

            this.driver = driver;
            this.assetPath = assetPath;
        }

        public abstract void render();
    }

    static class VideoAsset extends DigitalAsset {
        public VideoAsset(StorageDriver driver, String assetPath) {
            super(driver, assetPath);
        }

        @Override
        public void render() {
            System.out.println("Attempting to download Video Player: " + assetPath);

            byte[] data = driver.loadRawBlob(assetPath);

            if (data.length > 0) {
                System.out.println("SUCCESS: Video frame buffer synchronized: "
                        + data.length + " bytes via " + driver.getStorageId());
            } else {
                System.out.println("FAILURE: Video buffer empty. Playback aborted.");
            }
        }
    }

    static class AudioAsset extends DigitalAsset {
        public AudioAsset(StorageDriver driver, String assetPath) {
            super(driver, assetPath);
        }

        @Override
        public void render() {
            System.out.println("Attempting to download Audio Stream: " + assetPath);

            byte[] data = driver.loadRawBlob(assetPath);

            if (data.length > 0) {
                System.out.println("SUCCESS: Audio stream synchronized: "
                        + data.length + " bytes via " + driver.getStorageId());
            } else {
                System.out.println("FAILURE: Audio stream inaccessible.");
            }
        }
    }

    public static void main(String[] args) {
        StorageDriver s3 = new S3StorageDriver();
        StorageDriver nfs = new NfsStorageDriver();

        UserSession admin = new UserSession("root", "ADMIN");
        UserSession guest = new UserSession("anonymous", "GUEST");

        System.out.println(">>> 1. Authorized Video Playback via S3 (Admin Role)");

        DigitalAsset video = new VideoAsset(
                new StorageDriverProxy(s3, admin),
                "media/video.mp4");

        video.render();

        System.out.println(">>> 2. Unauthorized Audio Streaming via NFS (Guest Role)");

        DigitalAsset audio = new AudioAsset(
                new StorageDriverProxy(nfs, guest),
                "media/audio/track.flac");

        audio.render();
    }
}
