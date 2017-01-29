package no.ntnu.stud.minvakt.controller.salary;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A Java EE context listener which handles deploy-time initializations
 */
public class SalaryServletContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Scheduler scheduler;

    /**
     * Called when servlet is being deployed. Starts the QUARTZ scheduler
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Start the scheduler
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            // define the job and tie it to our MyJob class
            JobDetail job = newJob(SalaryListController.class)
                    .withIdentity("Salary list job", "mainGroup")
                    .build();

//            JobDetail debugJob = newJob(SalaryListController.class)
//                    .withIdentity("Salary list job", "debugGroup")
//                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("Salary list monthly trigger", "mainGroup")
                    .withSchedule(cronSchedule("0 0 23 L * ?")) // fire on the last day of every month at 23:00
                    .build();

            // Debug trigger each minute
//            Trigger debugTrigger = newTrigger()
//                    .withIdentity("Salary list test trigger", "debugGroup")
//                    .startNow()
//                    .withSchedule(cronSchedule("0 0/1 * 1/1 * ? *"))
//                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
//            scheduler.scheduleJob(debugJob, debugTrigger);
            log.info("Salary list job has been scheduled, will trigger on " + trigger.getNextFireTime());
        } catch (SchedulerException e) {
            log.log(Level.SEVERE, "Could not start QUARTZ scheduler", e);
        }
    }

    /**
     * Called when the servlet is stopping, shuts down the QUARTZ scheduler
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            log.log(Level.SEVERE, "Could not shut down QUARTZ scheduler", e);
        }
    }
}