import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacultyInfoQueryTest {

    private static final CsvMapper mapper = new CsvMapper();
    private static File csv;

    @BeforeAll
    static void setUpOnce() {
        csv = new File(FacultyInfoQuery.FILE_NAME);
        if (!csv.canRead()) {
            fail();
        }
    }

    @BeforeEach
    public void setUp() {
        FacultyInfoQuery.facultyList.clear();
        FacultyInfoQuery.readFile();
    }


    @Test
    public void testReadFile() {
        assertEquals(new Faculty("Cheng Jun Kee", "Research Assistant Professor", "Grubbs Institute"), FacultyInfoQuery.facultyList.get(0));
        assertEquals(new Faculty("HUAN Hu", "Assistant Professor", "School of Microelectronics"), FacultyInfoQuery.facultyList.get(1));
        assertEquals(new Faculty("陈廷勇", "Researche", "Institute for Quanfum Science and Engineering"), FacultyInfoQuery.facultyList.get(19));
        assertEquals(new Faculty("ZU Jiahe", "Professor", "Political Education and Research Center"), FacultyInfoQuery.facultyList.get(FacultyInfoQuery.facultyList.size() - 1));
    }

    @Test
    public void testHandleNameCommand() {
        final String existsName = "YU Shiqi";
        final String expectExist = "YU Shiqi, Associate Professor, Computer Science and Engineering";

        final String notExistsName = "WANG Daxing";
        final String expectNotExists = "No results";

        assertEquals(expectExist, FacultyInfoQuery.handleNameCommand(existsName));
        assertEquals(expectNotExists, FacultyInfoQuery.handleNameCommand(notExistsName));

        try (MappingIterator<Faculty> facultyIter = mapper
                .readerWithTypedSchemaFor(Faculty.class)
                .readValues(csv)) {
            while (facultyIter.hasNext()) {
                String name = facultyIter.next().getName();
                assertNotEquals(expectNotExists, FacultyInfoQuery.handleNameCommand(name));
            }
        } catch (IOException | NoSuchElementException e) {
            fail(e);
        }
    }

    @Test
    public void handleFirstLetterCommand() {
        final String badUtf8 = "���";
        final String number = "12.05";
        final String expectNotExists = "No results";

        assertEquals(expectNotExists, FacultyInfoQuery.handleFirstLetterCommand(badUtf8));
        assertEquals(expectNotExists, FacultyInfoQuery.handleFirstLetterCommand(number));

        final String startsWithO = "o";
        final String expected = """
                Oscar Lung Wa CHUNG, Professor, Chemistry
                OU Gang, Professor of Engineering Practice, School of System Design and Intelligent Manufacturing
                OU Xijun, 助理教授, Biology""";
        assertEquals(expected, FacultyInfoQuery.handleFirstLetterCommand(startsWithO));
    }

    @Test
    public void handleDepCommand() {
        final String dep = "Art Center";
        final String notExistsDep = "Conputer Science";

        final String depExpected = """
                BI Baoyi, Center Director, Art Center
                JI Tao, Music Teacher, Art Center
                LIU Hui, Chair Professor, Art Center
                LIU Keting, Music Teacher, Art Center
                PI Sheng, Music Theory Teacher, Art Center
                WEN Ying, Associate Professor, Art Center
                Zhou Mingcong, Associate Professor, Art Center""";
        final String nDepExpected = "No results";

        assertEquals(depExpected, FacultyInfoQuery.handleDepCommand(dep));
        assertEquals(nDepExpected, FacultyInfoQuery.handleDepCommand(notExistsDep));
    }

    @Test
    public void testHandleCommand() {
        final String INVALID = "Invalid command";

        final String name0arg = "NAME";
        final String name1arg = "NAME YU Shiqi";

        final String first0arg = "FIRSTLETTER";
        final String first1arg = "FIRSTLETTER Y";

        final String dep0arg = "DEP";
        final String dep1arg = "DEP Computer Science";

        final String undefCmd = "foo";

        assertEquals(INVALID, FacultyInfoQuery.handleCommand(name0arg));
        assertNotEquals(INVALID, FacultyInfoQuery.handleCommand(name1arg));

        assertEquals(INVALID, FacultyInfoQuery.handleCommand(first0arg));
        assertNotEquals(INVALID, FacultyInfoQuery.handleCommand(first1arg));

        assertEquals(INVALID, FacultyInfoQuery.handleCommand(dep0arg));
        assertNotEquals(INVALID, FacultyInfoQuery.handleCommand(dep1arg));

        assertEquals(INVALID, FacultyInfoQuery.handleCommand(undefCmd));
    }
}