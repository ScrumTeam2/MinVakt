package no.ntnu.stud.minvakt;

/**
 * Created by evend on 1/10/2017.
 */

import no.ntnu.stud.minvakt.services.ShiftServiceTest;
import no.ntnu.stud.minvakt.database.ShiftDBManagerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//JUnit Suite Test
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ShiftDBManagerTest.class,
        ShiftServiceTest.class,
//        SessionServiceTest.class,
        //TestJUnitEncryption.class,
        //TestJUnitDelivery.class,
})
public class JUnitTestSuit {
}
