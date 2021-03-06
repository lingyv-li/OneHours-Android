package com.guanmu.onehours;

import org.greenrobot.greendao.annotation.*;

import java.util.List;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.converter.PropertyConverter;
import com.guanmu.onehours.model.MPoint.Type;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "DPOINT".
 */
@Entity(active = true)
public class DPoint {

    @Id
    private String GUID;

    private String content;
    private String metaContent;

    @Convert(converter = DPoint.TypeConverter.class, columnType = String.class)
    private com.guanmu.onehours.model.MPoint.Type type;

    public static class TypeConverter implements PropertyConverter<com.guanmu.onehours.model.MPoint.Type, String> {
        @Override
        public com.guanmu.onehours.model.MPoint.Type convertToEntityProperty(String databaseValue) {
            return com.guanmu.onehours.model.MPoint.Type.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(com.guanmu.onehours.model.MPoint.Type entityProperty) {
            return entityProperty.name();
        }
    }

    @NotNull
    private int proficiency;
    @NotNull
    private long averageTime;
    @NotNull
    private int count;
    private String belong;
    @Convert(converter = DPoint.SyncStateConverter.class, columnType = String.class)
    private DPoint.SyncState syncState;

    public enum SyncState {
        synced, deleted, changed
    }

    public static class SyncStateConverter implements PropertyConverter<DPoint.SyncState, String> {
        @Override
        public DPoint.SyncState convertToEntityProperty(String databaseValue) {
            return DPoint.SyncState.valueOf(databaseValue);
        }

        @Override
        public String convertToDatabaseValue(DPoint.SyncState entityProperty) {
            return entityProperty.name();
        }
    }

    private java.util.Date lastUpdated;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;


    @ToMany(joinProperties = {
            @JoinProperty(name = "GUID", referencedName = "pointId")
    })
    private List<DLinkTaskPoint> dLinkTaskList;

    @ToMany
    @JoinEntity(entity=DLinkTaskPoint.class, sourceProperty = "pointId", targetProperty = "taskId")
    private List<DTask> dTaskList;

    @ToMany(joinProperties = {
            @JoinProperty(name = "GUID", referencedName = "pointId")
    })
    private List<DLinkTagPoint> dLinkTagList;

    @ToMany
    @JoinEntity(entity=DLinkTagPoint.class, sourceProperty = "pointId", targetProperty = "tagId")
    private List<DTag> dTagList;

    /** Used for active entity operations. */
    @Generated(hash = 1433655746)
    private transient DPointDao myDao;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    @Generated(hash = 833356338)
    public DPoint() {
    }

    public DPoint(String GUID) {
        this.GUID = GUID;
    }

    @Generated(hash = 1552401195)
    public DPoint(String GUID, String content, String metaContent, com.guanmu.onehours.model.MPoint.Type type,
            int proficiency, long averageTime, int count, String belong, DPoint.SyncState syncState,
            java.util.Date lastUpdated) {
        this.GUID = GUID;
        this.content = content;
        this.metaContent = metaContent;
        this.type = type;
        this.proficiency = proficiency;
        this.averageTime = averageTime;
        this.count = count;
        this.belong = belong;
        this.syncState = syncState;
        this.lastUpdated = lastUpdated;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    @NotNull
    public String getContent() {
        return content;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public String getMetaContent() {
        return metaContent;
    }

    public void setMetaContent(String metaContent) {
        this.metaContent = metaContent;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public java.util.Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(java.util.Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 724104686)
    public synchronized void resetDTagList() {
        dTagList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 210480873)
    public List<DTask> getDTaskList() {
        if (dTaskList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DTaskDao targetDao = daoSession.getDTaskDao();
            List<DTask> dTaskListNew = targetDao._queryDPoint_DTaskList(GUID);
            synchronized (this) {
                if(dTaskList == null) {
                    dTaskList = dTaskListNew;
                }
            }
        }
        return dTaskList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 217488085)
    public synchronized void resetDTaskList() {
        dTaskList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 105884148)
    public List<DTag> getDTagList() {
        if (dTagList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DTagDao targetDao = daoSession.getDTagDao();
            List<DTag> dTagListNew = targetDao._queryDPoint_DTagList(GUID);
            synchronized (this) {
                if(dTagList == null) {
                    dTagList = dTagListNew;
                }
            }
        }
        return dTagList;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 522723560)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDPointDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1029212468)
    public List<DLinkTaskPoint> getDLinkTaskList() {
        if (dLinkTaskList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DLinkTaskPointDao targetDao = daoSession.getDLinkTaskPointDao();
            List<DLinkTaskPoint> dLinkTaskListNew = targetDao._queryDPoint_DLinkTaskList(GUID);
            synchronized (this) {
                if(dLinkTaskList == null) {
                    dLinkTaskList = dLinkTaskListNew;
                }
            }
        }
        return dLinkTaskList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1008476038)
    public synchronized void resetDLinkTaskList() {
        dLinkTaskList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 7254645)
    public List<DLinkTagPoint> getDLinkTagList() {
        if (dLinkTagList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DLinkTagPointDao targetDao = daoSession.getDLinkTagPointDao();
            List<DLinkTagPoint> dLinkTagListNew = targetDao._queryDPoint_DLinkTagList(GUID);
            synchronized (this) {
                if(dLinkTagList == null) {
                    dLinkTagList = dLinkTagListNew;
                }
            }
        }
        return dLinkTagList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 105381331)
    public synchronized void resetDLinkTagList() {
        dLinkTagList = null;
    }

    public void setType(com.guanmu.onehours.model.MPoint.Type type) {
        this.type = type;
    }

    public com.guanmu.onehours.model.MPoint.Type getType() {
        return this.type;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DPoint.SyncState getSyncState() {
        return this.syncState;
    }

    public void setSyncState(DPoint.SyncState syncState) {
        this.syncState = syncState;
    }

    public int getProficiency() {
        return this.proficiency;
    }

    public long getAverageTime() {
        return this.averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public int getCount() {
        return this.count;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
