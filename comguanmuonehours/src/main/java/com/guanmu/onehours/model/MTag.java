package com.guanmu.onehours.model;

import com.guanmu.onehours.DLinkTagPoint;
import com.guanmu.onehours.DLinkTagPointDao;
import com.guanmu.onehours.DTag;
import com.guanmu.onehours.DTagDao;
import com.guanmu.onehours.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Larry.
 */
public class MTag {
    private DaoSession daoSession;
    private DTagDao dao;
    private DLinkTagPointDao pointDao;

    private String name;
    private String belong;

    private MTag parent;
    private List<MTag> children;
    private List<MPoint> points;

    public MTag(DaoSession dao) {
        this(dao, null, null, null);
    }

    public MTag(DaoSession dao, String name, String belong) {
        this(dao,
                tryGetGUID(dao.getDTagDao(), name, belong),
                name,
                belong);
    }

    private MTag(DaoSession dao, DTag dTag) {
        this(dao,
                UUID.fromString(dTag.getGUID()),
                dTag.getName(),
                dTag.getBelong()
        );
    }

    private MTag(DaoSession dao, UUID GUID, String name, String belong) {
        this.daoSession = dao;
        this.dao = dao.getDTagDao();
        this.pointDao = dao.getDLinkTagPointDao();
        this.name = name != null ? name : "";
        this.belong = belong;

        children = new ArrayList<>();
        points = new ArrayList<>();
        save();
    }


    public static List<MTag> loadAll(DaoSession dao) {
        return loadAll(dao, false, null);
    }

    public static List<MTag> loadAll(DaoSession dao, boolean filterBelong, @Nullable String belong) {
        cleanUnrelated(dao);
        QueryBuilder<DTag> qb = dao.getDTagDao().queryBuilder();
        if (filterBelong) {
            if (belong == null) {
                qb = qb.where(DTagDao.Properties.Belong.isNull());
            } else {
                qb = qb.where(DTagDao.Properties.Belong.eq(belong));
            }
        }
        return listFrom(dao, qb.list());
    }

    private static void cleanUnrelated(DaoSession dao) {
        for (DTag dTag
                : dao.getDTagDao().loadAll()) {
            if (dTag.getDLinkPointList().isEmpty()) {
                dTag.delete();
            }
        }
    }


    private static UUID tryGetGUID(DTagDao dao, String name, String belong) {
        try {
            QueryBuilder<DTag> qb = dao.queryBuilder().where(DTagDao.Properties.Name.eq(name));
            if (belong == null) {
                qb = qb.where(DTagDao.Properties.Belong.isNull());
            } else {
                qb = qb.where(DTagDao.Properties.Belong.eq(belong));
            }
            return UUID.fromString(qb.unique().getGUID());
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    static List<MTag> listFrom(DaoSession daoSession, @Nullable List<DTag> dTagList) {
        List<MTag> result = new ArrayList<>();
        if (dTagList != null) {
            for (DTag dTag :
                    dTagList) {
                result.add(new MTag(daoSession, dTag));
            }
        }
        return result;
    }

    public UUID getGUID() {
        return UUID.fromString(getD().getGUID());
    }

    @NonNull
    private DTag getD() {
        UUID GUID = tryGetGUID(dao, name, belong);
        if (GUID != null) {
            return dao.load(GUID.toString());
        } else {
            DTag dTag = new DTag(UUID.randomUUID().toString());
            dTag.setName(name);
            dTag.setBelong(belong);
            if (parent != null) {
                dTag.setParent(parent.getD());
            }
            dao.insert(dTag);
            return dTag;
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }

    public List<MPoint> getPoints() {
        return MPoint.listFrom(daoSession, getD().getDPointList());
    }

    public void setPoint(@NonNull MPoint... points) {
        setPoint(Arrays.asList(points));
    }

    public void setPoint(@NonNull List<MPoint> points) {
        removePoint(MPoint.listFrom(daoSession, getD().getDPointList()));
        addPoint(points);
    }

    public void addPoint(@NonNull MPoint... points) {
        addPoint(Arrays.asList(points));
    }

    public void addPoint(@NonNull List<MPoint> points) {
        DTag dTag = getD();
        QueryBuilder qb = pointDao.queryBuilder().where(DLinkTagPointDao.Properties.TagId.eq(dTag.getGUID()));
        for (MPoint point :
                points) {
            if (qb.where(DLinkTagPointDao.Properties.PointId.eq(point.getGUID().toString())).count() == 0) {
                DLinkTagPoint link = new DLinkTagPoint();
                link.setPointId(point.getGUID().toString());
                link.setTagId(dTag.getGUID());
                pointDao.save(link);
            }
        }
    }

    public void removePoint(@NonNull MPoint... points) {
        removePoint(Arrays.asList(points));
    }

    public void removePoint(@NonNull List<MPoint> points) {
        List<String> pointsId = new ArrayList<>();
        for (MPoint point :
                points) {
            pointsId.add(point.getGUID().toString());
        }
        pointDao.queryBuilder()
                .where(DLinkTagPointDao.Properties.TagId.eq(getD().getGUID()))
                .where(DLinkTagPointDao.Properties.PointId.in(pointsId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public List<MTag> getChildren() {
        return listFrom(daoSession, getD().getChildren());
    }

    public void setChildren(@NonNull List<MTag> children) {
        removeChildren(listFrom(daoSession, getD().getChildren()));
        addChildren(children);
    }

    public void setChildren(@NonNull MTag... children) {
        setChildren(Arrays.asList(children));
    }

    public void addChildren(@NonNull MTag... children) {
        addChildren(Arrays.asList(children));
    }

    public void addChildren(@NonNull List<MTag> children) {
        for (MTag child :
                children) {
            child.setParent(this);
        }
    }

    public void removeChildren(@NonNull MTag... children) {
        removeChildren(Arrays.asList(children));
    }

    public void removeChildren(@NonNull List<MTag> children) {
        for (MTag child :
                children) {
            if (child.getParent() == this) {
                child.setParent(null);
            }
        }
    }

    public MTag getParent() {
        return new MTag(daoSession, getD().getParent());
    }

    public void setParent(@Nullable MTag parent) {
        DTag dTag = getD();
        if (parent != null) {
            dTag.setParentId(parent.getD().getGUID());
        }
    }

    public void save() {
        DTag dTag = getD();
        dTag.setName(name);
        dTag.setBelong(belong);
        dao.save(dTag);
    }

    public void delete() {
        UUID GUID = tryGetGUID(dao, name, belong);
        if (GUID != null) {
            dao.deleteByKey(GUID.toString());
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
