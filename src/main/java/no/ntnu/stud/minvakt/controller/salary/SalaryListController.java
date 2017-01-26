package no.ntnu.stud.minvakt.controller.salary;

import no.ntnu.stud.minvakt.controller.email.Mail;
import no.ntnu.stud.minvakt.data.UserWorkInfo;
import no.ntnu.stud.minvakt.database.SalaryDBManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Audun on 26.01.2017.
 */
public class SalaryListController implements Job {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Salary list saver running!");
        generateSalaryList();
    }

    private void generateSalaryList() {
        SalaryDBManager dbManager = new SalaryDBManager();
        Map<Integer, UserWorkInfo> workInfo = dbManager.getAllWorkHours();

        StringBuilder csvBuilder = new StringBuilder("Fornavn;Etternavn;Ordinære timer;Overtid (timer)\n");
        for(UserWorkInfo user : workInfo.values()) {
            csvBuilder.append(user.getCsvString());
        }

        Mail.sendMailWithAttachment("system.minvakt@gmail.com", "Timeliste for " + LocalDate.now().withDayOfMonth(1) + " til " + LocalDate.now(), "Dette er en automatisk generert e-post.\nVedlagt ligger timeliste for " + LocalDate.now().withDayOfMonth(1) + " til " + LocalDate.now() + ".", "Timeliste.csv", csvBuilder.toString());
    }

//    public static void main(String[] args) {
//        new SalaryListController().generateSalaryList();
//    }
}
