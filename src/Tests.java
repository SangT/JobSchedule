import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class Tests
{
    private JobSchedule schedule;
    private JobSchedule.Job j1;

    @Before
    public void setUp() throws Exception
    {
        schedule = new JobSchedule();
        schedule.addJob(8); //adds job 0 with time 8
        j1 = schedule.addJob(3); //adds job 1 with time 3
        schedule.addJob(5); //adds job 2 with time 5
    }

    @Test
    public void testMinCompletionTime()
    {
        assertEquals(8, schedule.minCompletionTime());
        schedule.getJob(0).requires(schedule.getJob(2)); //job 2 must precede job 0
        assertEquals(13, schedule.minCompletionTime());
        schedule.getJob(0).requires(j1); //job 1 must precede job 0
        assertEquals(13, schedule.minCompletionTime()); //should return 13
        j1.requires(schedule.getJob(2)); //job 2 must precede job 1
        assertEquals(16, schedule.minCompletionTime());
        schedule.getJob(1).requires(schedule.getJob(0)); //job 0 must precede job 1 (creates loop)
        assertEquals(-1, schedule.minCompletionTime()); //should return -1
    }

    @Test
    public void testGetStartTime()
    {
        schedule.getJob(0).requires(schedule.getJob(2)); //job 2 must precede job 0
        schedule.getJob(0).requires(j1); //job 1 must precede job 0
        assertEquals(5, schedule.getJob(0).getStartTime());
        assertEquals(0, j1.getStartTime());
        assertEquals(0, schedule.getJob(2).getStartTime());
        j1.requires(schedule.getJob(2)); //job 2 must precede job 1
        assertEquals(8, schedule.getJob(0).getStartTime());
        assertEquals(5, schedule.getJob(1).getStartTime());
        assertEquals(0, schedule.getJob(2).getStartTime());
        schedule.getJob(1).requires(schedule.getJob(0)); //job 0 must precede job 1 (creates loop)
        assertEquals(-1, schedule.getJob(0).getStartTime());
        assertEquals(-1, schedule.getJob(1).getStartTime());
        assertEquals(0, schedule.getJob(2).getStartTime());
    }
}
