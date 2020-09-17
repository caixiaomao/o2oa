package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;

class ActionTypeSuspend extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTypeSuspend.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId) throws Exception {
		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			job = work.getJob();
		}
		return ProcessPlatformExecutorFactory.get(job).submit(new CallableImpl(workId)).get();
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		public CallableImpl(String id) {
			this.id = id;
		}

		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				Snap snap = new Snap(work);
				snap.setProperties(snap(business, work.getId()));
				snap.setType(Snap.TYPE_SUSPEND);
				emc.beginTransaction(Snap.class);
				emc.persist(snap, CheckPersistType.all);
				emc.commit();
				clean(business, work.getJob());
				Wo wo = new Wo();
				wo.setId(snap.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}



}