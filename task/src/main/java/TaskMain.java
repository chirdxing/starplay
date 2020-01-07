import com.star.common.context.SpringContextManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * task入口
 * @version 1.0
 */
public class TaskMain {
	private static Logger logger = LoggerFactory.getLogger(TaskMain.class);	
	
	static{
		try{
			ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
					"classpath*:spring/applicationContext-common.xml");
			new SpringContextManager().setApplicationContext(context);
			context.registerShutdownHook();
		}catch(Throwable e){
			logger.error("TaskMain startup error: " , e);
		}
	}
	
	public static void main(String[] args) {
		new TaskMain();
	}
}
