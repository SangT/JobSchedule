import java.util.*;

public class JobSchedule
{
    private final List<Job> jobs = new ArrayList<>();

    /* Time for the schedule to be completed. */
    private int minimumTime = 0;
    /* Flag to indicate if any changes were made to the graph. */
    private boolean changesMade = false;

    /* Magic number constant to indicate cycle(s). */
    private static final int CYCLIC = -1;

    public Job addJob(final int time)
    {
//        assert time >= 0;
        final Job j = new Job(time);
        jobs.add(j);

        /* If the graph is acyclic, check if time needs to be updated. */
        if (minimumTime != CYCLIC && time > minimumTime)
            minimumTime = time;

        return j;
    }

    public Job getJob(final int i)
    {
//        assert i >= 0;
//        assert i < jobs.size();
        return jobs.get(i);
    }

    /**
     * Calculates the minimum amount of time for the graph to be completed.
     * @return minimum completion time
     */
    public int minCompletionTime()
    {
        /* A graph that is cyclic will always be cyclic.
         * No changes to the graph means minimum time hasn't changed. */
        if (minimumTime == CYCLIC || !changesMade)
            return minimumTime;
        updateGraph();
//        assert !changesMade;
        return minCompletionTime();
    }

    private void updateGraph()
    {
//        assert changesMade;

        final List<Job> topoSorted = topologicalSort(jobs);

        for (final Job j : topoSorted)
            j.startTime = 0;
        for (final Job j : topoSorted)
            for (final Job v : j.outbound)
                v.startTime = Math.max(v.startTime, j.startTime + j.time);

        /* Once the graph is cyclic, it will always be cyclic. */
        if (minimumTime != CYCLIC)
        {
            if (jobs.size() == topoSorted.size())
            {
                minimumTime = 0;
                /* Find the new minimum completion time. */
                for (final Job j : topoSorted)
                    minimumTime = Math.max(minimumTime, j.startTime + j.time);
            }
            /* A differing number of nodes in each list means the graph is cyclic. */
            else minimumTime = CYCLIC;
        }

        changesMade = false;
    }

    /**
     * Produces a topologically sorted list of Jobs from a provided list.
     * Sorted via Kahn's Algorithm.
     * @param jobs - Jobs to be topologically sorted.
     * @return new topologically sorted list of Jobs.
     */
    private static List<Job> topologicalSort(final List<Job> jobs)
    {
//        assert jobs != null;
        /* Prepare all job's inDegrees to be incremented. */
        for (final Job u : jobs)
            u.inDegree = 0;
        /* Count how many jobs each job requires. */
        for (final Job u : jobs)
            for (final Job v : u.outbound)
                v.inDegree++;

        final List<Job> sorted = new ArrayList<>(jobs.size());
        /* Jobs which have no requirements are the entry points into the graph. */
        for (final Job u : jobs)
            if (u.inDegree == 0)
                sorted.add(u);

        for (int i = 0; i < sorted.size(); i++)
        {
            final Job u = sorted.get(i);
            for (final Job v : u.outbound)
                /* Remove edges until a node's requirements become exhausted. */
                if (--v.inDegree == 0)
                    sorted.add(v);
        }

        return sorted;
    }

    public final class Job
    {
        private final int time;
        private final List<Job> outbound;

        private int inDegree = 0;
        private int startTime = 0;

        private Job(final int time)
        {
            this.time = time;
            outbound = new ArrayList<>();
        }

        public void requires(final Job j)
        {
            j.outbound.add(this);
            changesMade = true;
        }

        /**
         * Gets the time it will take for this Job to begin.
         * @return time to begin or -1 if it is part of a cycle.
         */
        public int getStartTime()
        {
            /* A node which still has requirements after topological sort is cyclic.
             * Even if changes were made, once the node is cyclic, it will always be cyclic. */
            if (inDegree != 0)
                return CYCLIC;
            if (!changesMade)
                return startTime;
            updateGraph();
//            assert !changesMade;
            return getStartTime();
        }
    }
}