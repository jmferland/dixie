package dixie.dao;

import dixie.model.BaseModel;
import java.lang.reflect.ParameterizedType;

/**
 *
 * @author jferland
 */
public class BaseDao<MODEL extends BaseModel>
{

	protected DaoManager daoManager;
	protected Class<MODEL> modelClass;

	public BaseDao()
	{
		this.modelClass = (Class<MODEL>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	public Class<MODEL> getModelClass()
	{
		return this.modelClass;
	}
}
