package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

//JUnit Suite Test
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestJUnitDB.class,
        TestJUnitREST.class,
        //TestJUnitEncryption.class,
        //TestJUnitDelivery.class,
})
public class JUnitTestSuit {
}
