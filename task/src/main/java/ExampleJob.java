import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 示例定时任务
 * 用法：1、配置扫描；2、配置task驱动；3、配置@Compoent；4、配置Scheduled
 * @version 1.0
 */
@Component
public class ExampleJob {
	private static final Logger logger = LoggerFactory.getLogger(ExampleJob.class);	
	
	@Scheduled(cron = "0/30 * * * * ?") // 每隔30秒执行一次
    public void execute() {
		logger.info("ExampleJob executing");
        try {
        	logger.info("hi，30秒钟又到啦，跑一次");
        } catch (Exception e) {
        	logger.error("ExampleJob execute error : " , e);
        }
    }
	
}
