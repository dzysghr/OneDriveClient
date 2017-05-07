package com.dzy.onedriveclient.model.drive;

/**
 * Created by dzysg on 2017/5/7 0007.
 */

public class Drive {


    /**
     * id : 0123456789abc
     * driveType : personal
     * owner : {"user":{"id":"12391913bac","displayName":"Ryan Gregg"}}
     * quota : {"total":1024000,"used":514000,"remaining":1010112,"deleted":0,"state":"normal"}
     */

    private String id;
    private String driveType;
    private OwnerBean owner;
    private QuotaBean quota;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public OwnerBean getOwner() {
        return owner;
    }

    public void setOwner(OwnerBean owner) {
        this.owner = owner;
    }

    public QuotaBean getQuota() {
        return quota;
    }

    public void setQuota(QuotaBean quota) {
        this.quota = quota;
    }

    public static class OwnerBean {
        /**
         * user : {"id":"12391913bac","displayName":"Ryan Gregg"}
         */

        private UserBean user;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * id : 12391913bac
             * displayName : Ryan Gregg
             */

            private String id;
            private String displayName;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }
        }
    }

    public static class QuotaBean {
        /**
         * total : 1024000
         * used : 514000
         * remaining : 1010112
         * deleted : 0
         * state : normal
         */

        private long total;
        private long used;
        private long remaining;
        private long deleted;
        private String state;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getUsed() {
            return used;
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public long getRemaining() {
            return remaining;
        }

        public void setRemaining(long remaining) {
            this.remaining = remaining;
        }

        public long getDeleted() {
            return deleted;
        }

        public void setDeleted(long deleted) {
            this.deleted = deleted;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
