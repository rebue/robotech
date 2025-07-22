package rebue.robotech.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.SqlSessionTemplate;

public class MyBatisUtils {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBatchExecutor(SqlSessionTemplate sqlSessionTemplate) {
        return sqlSessionTemplate.getConfiguration().getDefaultExecutorType() == ExecutorType.BATCH;
    }

}
