package com.guanmu.onehours.model;

import android.text.TextUtils;

import com.guanmu.onehours.DLinkTagPoint;
import com.guanmu.onehours.DLinkTagPointDao;
import com.guanmu.onehours.DPoint;
import com.guanmu.onehours.DPointDao;
import com.guanmu.onehours.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.guanmu.onehours.ObjectUtils.coalesce;

/**
 * Created by Larry.
 */
public class MPoint {
    private DaoSession daoSession;
    private DPointDao dao;
    private DLinkTagPointDao tagDao;

    private UUID GUID;
    private long averageTime;
    private String belong;
    private String content;
    private int count;
    private String metaContent;
    private int proficiency;
    private Type type;
    private List<MTag> addTagList;
    private List<MTag> removeTagList;

    public MPoint(DaoSession dao) {
        this(dao, UUID.randomUUID(), 0, null, null, 0, null, 0, null);
    }

    public MPoint(DaoSession dao, @NonNull UUID GUID) {
        this(dao,
                coalesce(dao.getDPointDao().load(GUID.toString()),
                        new DPoint(GUID.toString()))
        );
    }

    private MPoint(DaoSession dao, @Nullable DPoint dPoint) {
        this(dao,
                UUID.fromString(dPoint != null ? dPoint.getGUID() : UUID.randomUUID().toString()),
                dPoint != null ? dPoint.getAverageTime() : 0,
                dPoint != null ? dPoint.getBelong() : null,
                dPoint != null ? dPoint.getContent() : null,
                dPoint != null ? dPoint.getCount() : 0,
                dPoint != null ? dPoint.getMetaContent() : null,
                dPoint != null ? dPoint.getProficiency() : 0,
                dPoint != null ? dPoint.getType() : null
        );
    }

    private MPoint(DaoSession dao, @Nullable UUID GUID, long averageTime, @Nullable String belong, @Nullable String content, int count, @Nullable String metaContent, int proficiency, @Nullable Type type) {
        this.daoSession = dao;
        this.dao = dao.getDPointDao();
        this.tagDao = dao.getDLinkTagPointDao();

        this.GUID = GUID != null ? GUID : UUID.randomUUID();
        this.averageTime = averageTime;
        this.belong = belong;
        this.content = content != null ? content : "";
        this.metaContent = metaContent != null ? metaContent : "";
        this.count = count;
        this.proficiency = proficiency;
        this.type = type != null ? type : Type.normal;

        this.addTagList = new ArrayList<>();
        this.removeTagList = new ArrayList<>();
    }

    public static List<MPoint> loadAll(DaoSession dao) {
        return loadAll(dao, null, false, null);
    }

    public static List<MPoint> loadAscProf(DaoSession dao, int n) {
        QueryBuilder<DPoint> qb = dao.getDPointDao().queryBuilder()
                .where(DPointDao.Properties.Belong.isNull())
                .orderAsc(DPointDao.Properties.Proficiency)
                .limit(n);
        return listFrom(dao, qb.list());

    }

    public static List<MPoint> loadAll(DaoSession dao, @Nullable String searchString, boolean filterBelong, @Nullable String belong) {
        QueryBuilder<DPoint> qb = dao.getDPointDao().queryBuilder();
        if (filterBelong) {
            if (belong == null) {
                qb = qb.where(DPointDao.Properties.Belong.isNull());
            } else {
                qb = qb.where(DPointDao.Properties.Belong.eq(belong));
            }
        }
        if (!TextUtils.isEmpty(searchString)) {
            qb = qb.where(DPointDao.Properties.Content.like("%" + searchString.replace(' ', '%') + "%"));
        }
        return listFrom(dao, qb.list());
    }

    public static long countAll(DaoSession dao) {
        return dao.getDPointDao().queryBuilder().where(DPointDao.Properties.Belong.isNull()).count();
    }

    public static long countProf(DaoSession dao) {
        return dao.getDPointDao().queryBuilder().where(
                DPointDao.Properties.Belong.isNull(),
                DPointDao.Properties.Proficiency.lt(5)
        ).count();
    }

    static List<MPoint> listFrom(DaoSession dao, List<DPoint> dPointList) {
        List<MPoint> result = new ArrayList<>();
        for (DPoint dPoint :
                dPointList) {
            result.add(new MPoint(dao, dPoint));
        }
        return result;
    }

    private DPoint getD() {
        return dao.load(GUID.toString());
    }

    public UUID getGUID() {
        return GUID;
    }

    public void setGUID(UUID GUID) {
        this.GUID = GUID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMetaContent() {
        return metaContent;
    }

    public void setMetaContent(String metaContent) {
        this.metaContent = metaContent;
    }

    public int getProficiency() {
        return proficiency;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public List<MTag> getAddTagList() {
        if (getD() != null) {
            return MTag.listFrom(daoSession, getD().getDTagList());
        } else {
            return this.addTagList;
        }
    }


    public void setTag(@NonNull MTag... tags) {
        setTag(Arrays.asList(tags));
    }

    public void setTag(@NonNull List<MTag> tags) {
        if (getD() != null) {
            removeTag(MTag.listFrom(daoSession, getD().getDTagList()));
            addTag(tags);
        } else {
            this.addTagList.clear();
            this.addTagList.addAll(tags);
            this.removeTagList = new ArrayList<>();
        }
    }

    public void addTag(@NonNull MTag... tags) {
        addTag(Arrays.asList(tags));
    }

    public void addTag(@NonNull List<MTag> tags) {
        if (getD() != null) {
            QueryBuilder qb = tagDao.queryBuilder().where(DLinkTagPointDao.Properties.PointId.eq(this.GUID.toString()));
            for (MTag tag :
                    tags) {
                if (qb.where(DLinkTagPointDao.Properties.TagId.eq(tag.getGUID().toString())).count() == 0) {
                    DLinkTagPoint link = new DLinkTagPoint();
                    link.setPointId(this.GUID.toString());
                    link.setTagId(tag.getGUID().toString());
                    tagDao.save(link);
                }
            }
        } else {
            this.addTagList.addAll(tags);
        }
    }


    public void removeTag(@NonNull MTag... tags) {
        removeTag(Arrays.asList(tags));
    }

    public void removeTag(@NonNull List<MTag> tags) {
        if (getD() != null) {
            List<String> tagsId = new ArrayList<>();
            for (MTag tag :
                    tags) {
                tagsId.add(tag.getGUID().toString());
            }
            tagDao.queryBuilder()
                    .where(DLinkTagPointDao.Properties.PointId.eq(this.GUID.toString()))
                    .where(DLinkTagPointDao.Properties.TagId.in(tagsId))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
            // TODO clean not related objects
        } else {
            this.removeTagList.addAll(tags);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void save() {
        DPoint dPoint = dao.load(GUID.toString());
        if (dPoint == null) {
            dPoint = new DPoint(GUID.toString());
            dao.insert(dPoint);

            addTag(this.addTagList);
            removeTag(this.removeTagList);
        }
        dPoint.setAverageTime(averageTime);
        dPoint.setBelong(belong);
        dPoint.setContent(content);
        dPoint.setCount(count);
        dPoint.setLastUpdated(new Date());
        dPoint.setMetaContent(metaContent);
        dPoint.setProficiency(proficiency);
        dPoint.setType(type);
        dPoint.setSyncState(DPoint.SyncState.changed);
        dao.save(dPoint);
    }

    public void delete() {
        dao.deleteByKey(GUID.toString());
    }

    public enum Type {normal, image}
}
