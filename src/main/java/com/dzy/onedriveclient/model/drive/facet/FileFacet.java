package com.dzy.onedriveclient.model.drive.facet;

public class FileFacet {

    /**
     * mimeType : string
     * hashes : {"@odata.type":"oneDrive.hashes"}
     * processingMetadata : false
     */

    private String mimeType;
    private HashesBean hashes;
    private boolean processingMetadata;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public HashesBean getHashes() {
        return hashes;
    }

    public void setHashes(HashesBean hashes) {
        this.hashes = hashes;
    }

    public boolean isProcessingMetadata() {
        return processingMetadata;
    }

    public void setProcessingMetadata(boolean processingMetadata) {
        this.processingMetadata = processingMetadata;
    }

    public static class HashesBean {
        private String crc32Hash;
        private String sha1Hash;
        private String quickXorHash;

        public String getCrc32Hash() {
            return crc32Hash;
        }

        public void setCrc32Hash(String crc32Hash) {
            this.crc32Hash = crc32Hash;
        }

        public String getSha1Hash() {
            return sha1Hash;
        }

        public void setSha1Hash(String sha1Hash) {
            this.sha1Hash = sha1Hash;
        }

        public String getQuickXorHash() {
            return quickXorHash;
        }

        public void setQuickXorHash(String quickXorHash) {
            this.quickXorHash = quickXorHash;
        }
    }
}
