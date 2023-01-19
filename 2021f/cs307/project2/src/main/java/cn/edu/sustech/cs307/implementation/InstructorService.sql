CREATE OR REPLACE FUNCTION get_instructed_cs(iid integer, semid integer)
    RETURNS
        table
        (
            id       integer,
            secname  text,
            capacity integer,
            left_cap integer
        )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY (
        WITH csc AS (SELECT DISTINCT section FROM coursesectionclass WHERE instructor = iid)
        SELECT cs.id AS id, name AS secname, cs.capacity AS capacity, (capacity - chosenStu) AS left_cap
        FROM csc
                 JOIN coursesection cs ON csc.section = cs.id
        WHERE cs.semester = semid
    );
END;
$$;
