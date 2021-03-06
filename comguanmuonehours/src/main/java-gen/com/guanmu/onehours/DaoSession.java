package com.guanmu.onehours;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.guanmu.onehours.DLinkTagPoint;
import com.guanmu.onehours.DLinkTaskPoint;
import com.guanmu.onehours.DPoint;
import com.guanmu.onehours.DTag;
import com.guanmu.onehours.DTask;

import com.guanmu.onehours.DLinkTagPointDao;
import com.guanmu.onehours.DLinkTaskPointDao;
import com.guanmu.onehours.DPointDao;
import com.guanmu.onehours.DTagDao;
import com.guanmu.onehours.DTaskDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dLinkTagPointDaoConfig;
    private final DaoConfig dLinkTaskPointDaoConfig;
    private final DaoConfig dPointDaoConfig;
    private final DaoConfig dTagDaoConfig;
    private final DaoConfig dTaskDaoConfig;

    private final DLinkTagPointDao dLinkTagPointDao;
    private final DLinkTaskPointDao dLinkTaskPointDao;
    private final DPointDao dPointDao;
    private final DTagDao dTagDao;
    private final DTaskDao dTaskDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dLinkTagPointDaoConfig = daoConfigMap.get(DLinkTagPointDao.class).clone();
        dLinkTagPointDaoConfig.initIdentityScope(type);

        dLinkTaskPointDaoConfig = daoConfigMap.get(DLinkTaskPointDao.class).clone();
        dLinkTaskPointDaoConfig.initIdentityScope(type);

        dPointDaoConfig = daoConfigMap.get(DPointDao.class).clone();
        dPointDaoConfig.initIdentityScope(type);

        dTagDaoConfig = daoConfigMap.get(DTagDao.class).clone();
        dTagDaoConfig.initIdentityScope(type);

        dTaskDaoConfig = daoConfigMap.get(DTaskDao.class).clone();
        dTaskDaoConfig.initIdentityScope(type);

        dLinkTagPointDao = new DLinkTagPointDao(dLinkTagPointDaoConfig, this);
        dLinkTaskPointDao = new DLinkTaskPointDao(dLinkTaskPointDaoConfig, this);
        dPointDao = new DPointDao(dPointDaoConfig, this);
        dTagDao = new DTagDao(dTagDaoConfig, this);
        dTaskDao = new DTaskDao(dTaskDaoConfig, this);

        registerDao(DLinkTagPoint.class, dLinkTagPointDao);
        registerDao(DLinkTaskPoint.class, dLinkTaskPointDao);
        registerDao(DPoint.class, dPointDao);
        registerDao(DTag.class, dTagDao);
        registerDao(DTask.class, dTaskDao);
    }
    
    public void clear() {
        dLinkTagPointDaoConfig.clearIdentityScope();
        dLinkTaskPointDaoConfig.clearIdentityScope();
        dPointDaoConfig.clearIdentityScope();
        dTagDaoConfig.clearIdentityScope();
        dTaskDaoConfig.clearIdentityScope();
    }

    public DLinkTagPointDao getDLinkTagPointDao() {
        return dLinkTagPointDao;
    }

    public DLinkTaskPointDao getDLinkTaskPointDao() {
        return dLinkTaskPointDao;
    }

    public DPointDao getDPointDao() {
        return dPointDao;
    }

    public DTagDao getDTagDao() {
        return dTagDao;
    }

    public DTaskDao getDTaskDao() {
        return dTaskDao;
    }

}
