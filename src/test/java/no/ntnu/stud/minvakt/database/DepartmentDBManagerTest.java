package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Department;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by evend on 1/25/2017.
 */
public class DepartmentDBManagerTest {

    private static DepartmentDBManager deptDB;

    @BeforeClass
    public static void DBsetUp() {
        deptDB = new DepartmentDBManager();
    }

    @Test
    public void getDepartement(){
        assertTrue(deptDB.getDepartment(1).getId() == 1);
    }
    @Test
    public void getDepartments(){
        assertFalse(deptDB.getDepartments().isEmpty());
    }
}
