# SQL interview practice: Spring Boot and MySQL

Start with the `course` table from your course. Build confidence in reading and changing rows, then practice joins, aggregation and common interview problems.

**Dialect:** MySQL 8+. CTEs and window functions require MySQL 8+. Examples are independent unless a section explicitly describes a sequence. Setup creates fresh practice tables; use a practice database where these names do not already exist.

**Practice method:** read the question → hide the query → write it → predict the rows → run it → explain the edge case.

## 1. Your exact queries, with comments

```sql
-- INSERT adds one row.
-- Listing columns explicitly makes each value's destination clear.
INSERT INTO course (id, name, author)
VALUES (1, 'Learn AWS', 'in28mins');

-- SELECT reads rows. * means all columns.
-- Run this BEFORE deleting if you want to see the new row.
SELECT *
FROM course;

-- DELETE removes every row matching the condition.
-- Here, id is a primary key, so at most one row matches.
DELETE FROM course
WHERE id = 1;

-- The row with id = 1 is now gone.
-- Other existing rows remain.
SELECT *
FROM course;
```

Your submitted sequence was `INSERT → DELETE → SELECT`. If the table started empty, the final result is empty.

```text
Empty course table
      ↓ INSERT (1, 'Learn AWS', 'in28mins')
One row
      ↓ SELECT
See that row
      ↓ DELETE WHERE id = 1
Empty table again
```

| Step | id | name | author |
| --- | --- | --- | --- |
| After INSERT | 1 | Learn AWS | in28mins |
| After DELETE | No rows, if the table originally contained no other rows | | |

**Memory hook:** `INSERT` adds, `SELECT` reads, `UPDATE` changes, `DELETE` removes. End each statement with `;` when running several statements together.

## 2. Practice schema and data

Run this setup instead of the previous demonstration when practicing the queries below. If you already have `course`, use a separate practice database to avoid name conflicts.

```sql
CREATE TABLE course (
    id INT PRIMARY KEY,       -- Unique row identifier; cannot be NULL.
    name VARCHAR(100) NOT NULL,
    author VARCHAR(100)       -- NULL represents a missing/unknown author.
);

CREATE TABLE learner (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE enrollment (
    learner_id INT NOT NULL,
    course_id INT NOT NULL,
    enrolled_on DATE NOT NULL,
    score DECIMAL(5,2),
    PRIMARY KEY (learner_id, course_id),
    FOREIGN KEY (learner_id) REFERENCES learner(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    CHECK (score BETWEEN 0 AND 100)
    -- NULL scores are permitted; CHECK rejects out-of-range non-NULL values.
);

INSERT INTO course (id, name, author) VALUES
    (1, 'Learn AWS', 'in28mins'),
    (2, 'Spring Boot', 'in28mins'),
    (3, 'Java Basics', 'Maya'),
    (4, 'SQL Essentials', NULL);

INSERT INTO learner (id, name, email) VALUES
    (10, 'Ava', 'ava@example.com'),
    (20, 'Ben', 'ben@example.com'),
    (30, 'Cara', 'cara@example.com'),
    (40, 'Dan', 'dan@example.com');

INSERT INTO enrollment
    (learner_id, course_id, enrolled_on, score) VALUES
    (10, 1, '2026-09-01', 90),
    (10, 2, '2026-09-02', 85),
    (20, 2, '2026-09-03', 85),
    (30, 3, '2026-09-04', NULL);
```

MySQL enforces `CHECK` constraints beginning with 8.0.16. The practice transaction examples assume InnoDB tables, the normal MySQL default.

### Tables you are querying

**course**

| id | name | author |
| --- | --- | --- |
| 1 | Learn AWS | in28mins |
| 2 | Spring Boot | in28mins |
| 3 | Java Basics | Maya |
| 4 | SQL Essentials | NULL |

**learner**

| id | name | email |
| --- | --- | --- |
| 10 | Ava | ava@example.com |
| 20 | Ben | ben@example.com |
| 30 | Cara | cara@example.com |
| 40 | Dan | dan@example.com |

**enrollment**

| learner_id | course_id | enrolled_on | score |
| --- | --- | --- | --- |
| 10 | 1 | 2026-09-01 | 90.00 |
| 10 | 2 | 2026-09-02 | 85.00 |
| 20 | 2 | 2026-09-03 | 85.00 |
| 30 | 3 | 2026-09-04 | NULL |

```text
learner.id                          course.id
    10 ──┐                     ┌── 1
         └→ enrollment (10, 1) ←┘
    10 ───→ enrollment (10, 2) ←── 2
    20 ───→ enrollment (20, 2) ←── 2
    30 ───→ enrollment (30, 3) ←── 3
    40      no enrollment           4 has no enrollment

One learner → many enrollments
One course  → many enrollments
Learners ↔ courses: many-to-many, connected by enrollment
```

## 3. Essential reads and filters

### Q1. Read specific columns

```sql
-- Prefer explicit columns in application queries.
-- ORDER BY makes presentation order deterministic.
SELECT id, name
FROM course
ORDER BY id;
```

### Q2. Find one course by its primary key

```sql
SELECT id, name, author
FROM course
WHERE id = 2; -- Spring Boot.
```

### Q3. Combine AND, OR and NOT

```sql
SELECT id, name, author
FROM course
WHERE (author = 'in28mins' OR author = 'Maya')
  AND id >= 2
ORDER BY id; -- IDs 2 and 3.
```

Parentheses make the intended logic clear. `AND` has higher precedence than `OR`.

### Q4. Match several values with IN

```sql
SELECT id, name
FROM course
WHERE id IN (1, 3)
ORDER BY id;
```

### Q5. Find a range with BETWEEN

```sql
SELECT learner_id, course_id, score
FROM enrollment
WHERE score BETWEEN 85 AND 90 -- Both endpoints are included.
ORDER BY learner_id, course_id;
```

### Q6. Search names with LIKE

```sql
SELECT id, name
FROM course
WHERE name LIKE 'Learn%'; -- Starts with Learn: Learn AWS.

-- '%' matches any sequence of characters.
-- '_' matches exactly one character.
-- Case sensitivity depends on the column's collation.
```

### Q7. Find missing values correctly

```sql
SELECT id, name
FROM course
WHERE author IS NULL; -- SQL Essentials.

-- author = NULL does not test for a missing value.
-- Use IS NOT NULL to find known authors.
```

### Q8. Show a fallback with COALESCE

```sql
SELECT id, name, COALESCE(author, 'Unknown') AS author_label
FROM course
ORDER BY id;
-- This changes the displayed result, not the stored data.
```

### Q9. List distinct authors

```sql
SELECT DISTINCT author
FROM course
WHERE author IS NOT NULL
ORDER BY author; -- in28mins and Maya; collation determines sorting.
```

`DISTINCT` applies to the complete selected row. `DISTINCT author, name` means unique author/name pairs.

### Q10. Get the highest scores with stable ordering

```sql
SELECT learner_id, course_id, score
FROM enrollment
WHERE score IS NOT NULL
ORDER BY score DESC, learner_id ASC, course_id ASC
LIMIT 2;
-- Results: (10,1,90), then (10,2,85).
-- Tie-breakers make the chosen two rows reproducible.
```

## 4. Joins: see the rows connect

| Join/pattern | Keeps | Typical question |
| --- | --- | --- |
| INNER JOIN | Matching pairs | Who is enrolled in which course? |
| LEFT JOIN | Every left row, including unmatched rows | Show every course, even with no learners |
| Self join | Related rows in the same table | Who earns more than their manager? |
| CROSS JOIN | Every possible pair | Generate learner/course combinations |
| NOT EXISTS | Left rows with no qualifying match | Which learners have no enrollment? |

### Q11. Join learners to enrollments to courses

```sql
SELECT l.name AS learner_name, c.name AS course_name, e.score
FROM learner AS l
JOIN enrollment AS e ON e.learner_id = l.id
JOIN course AS c ON c.id = e.course_id
ORDER BY l.id, c.id;
-- Aliases distinguish columns with the same name.
```

```text
learner               enrollment               course
Ava, id=10 ─────────→ (10, 1), score=90 ───────→ Learn AWS, id=1
          └─────────→ (10, 2), score=85 ───────→ Spring Boot, id=2

Input tables → match foreign keys → combined output rows
```

| learner_name | course_name | score |
| --- | --- | --- |
| Ava | Learn AWS | 90 |
| Ava | Spring Boot | 85 |
| Ben | Spring Boot | 85 |
| Cara | Java Basics | NULL |

Dan and SQL Essentials disappear from this inner join because they have no enrollment match.

### Q12. Keep courses even when nobody enrolled

```sql
SELECT c.id, c.name, e.learner_id
FROM course AS c
LEFT JOIN enrollment AS e ON e.course_id = c.id
ORDER BY c.id, e.learner_id;
```

| course | learner_id |
| --- | --- |
| Learn AWS | 10 |
| Spring Boot | 10 |
| Spring Boot | 20 |
| Java Basics | 30 |
| SQL Essentials | NULL |

```text
All course rows → LEFT JOIN → keep matches
                           → keep unmatched SQL Essentials + NULL fields
```

### Q13. Find learners without an enrollment

```sql
SELECT l.id, l.name
FROM learner AS l
WHERE NOT EXISTS (
    SELECT 1
    FROM enrollment AS e
    WHERE e.learner_id = l.id
)
ORDER BY l.id; -- Dan.
-- Correlation connects the subquery to the current learner.
```

Avoid `NOT IN` against a nullable subquery result: a NULL can make the predicate UNKNOWN and remove expected results.

### Q14. Preserve unmatched rows when filtering a LEFT JOIN

```sql
SELECT c.id, c.name, e.learner_id, e.score
FROM course AS c
LEFT JOIN enrollment AS e
    ON e.course_id = c.id
   AND e.score >= 90 -- Limits matches, but preserves every course.
ORDER BY c.id, e.learner_id;
```

```text
Filter in ON    → courses stay → only qualifying enrollments attach
Filter in WHERE → rows with NULL score fail → unmatched courses disappear
```

Moving `e.score >= 90` into `WHERE` would remove courses without a qualifying score. [MySQL join reference](https://dev.mysql.com/doc/refman/8.4/en/join.html)

## 5. Aggregation, GROUP BY and HAVING

### Q15. COUNT(*) versus COUNT(column)

```sql
SELECT COUNT(*) AS enrollment_rows,
       COUNT(score) AS scored_rows,
       AVG(score) AS average_score,
       MIN(score) AS lowest_score,
       MAX(score) AS highest_score
FROM enrollment;
-- COUNT(*) = 4; COUNT(score) = 3.
-- AVG(score) = 86.666...; MIN = 85; MAX = 90.
-- These aggregates ignore NULL score values.
```

With no matching rows, `COUNT` returns 0, while `SUM`, `AVG`, `MIN` and `MAX` return NULL.

### Q16. Count enrollments per course, including zero

```sql
SELECT c.id, c.name, COUNT(e.learner_id) AS learner_count
FROM course AS c
LEFT JOIN enrollment AS e ON e.course_id = c.id
GROUP BY c.id, c.name
ORDER BY c.id;
-- Count the matched column, not COUNT(*).
-- COUNT(*) would count the placeholder row for an unmatched course.
```

| course | learner_count |
| --- | --- |
| Learn AWS | 1 |
| Spring Boot | 2 |
| Java Basics | 1 |
| SQL Essentials | 0 |

```text
Joined rows → group by course → count non-NULL learner IDs → one row per course
```

### Q17. Filter groups with HAVING

```sql
SELECT course_id, COUNT(*) AS learner_count
FROM enrollment
WHERE enrolled_on >= '2026-09-01' -- Filters individual rows first.
GROUP BY course_id
HAVING COUNT(*) >= 2              -- Filters the resulting groups.
ORDER BY course_id; -- Course 2, count 2.
```

**Memory hook:** `WHERE` filters rows → `GROUP BY` builds groups → `HAVING` filters groups. Keep nonaggregated selected columns grouped or validly dependent on grouped keys; MySQL's default `ONLY_FULL_GROUP_BY` checks this. [MySQL GROUP BY reference](https://dev.mysql.com/doc/refman/8.4/en/group-by-handling.html)

### Q18. Conditional aggregation with CASE

```sql
SELECT course_id,
       SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) AS high_scores,
       SUM(CASE WHEN score IS NULL THEN 1 ELSE 0 END) AS unscored
FROM enrollment
GROUP BY course_id
ORDER BY course_id;
-- Course 1: (1,0); course 2: (0,0); course 3: (0,1).
```

## 6. Subqueries, CTEs and interview classics

### Q19. Find scores above the overall average

```sql
SELECT learner_id, course_id, score
FROM enrollment
WHERE score > (SELECT AVG(score) FROM enrollment)
ORDER BY learner_id, course_id;
-- Only Ava's 90 in course 1.
-- The scalar subquery produces one average value.
```

### Q20. Find learners enrolled in Spring Boot using EXISTS

```sql
SELECT l.id, l.name
FROM learner AS l
WHERE EXISTS (
    SELECT 1
    FROM enrollment AS e
    JOIN course AS c ON c.id = e.course_id
    WHERE e.learner_id = l.id
      AND c.name = 'Spring Boot'
)
ORDER BY l.id; -- Ava and Ben.
-- EXISTS tests whether a match exists without multiplying learner rows.
```

### Q21. Name a query result with a CTE

```sql
WITH course_averages AS (
    SELECT course_id, AVG(score) AS average_score
    FROM enrollment
    GROUP BY course_id
)
SELECT c.name, ca.average_score
FROM course_averages AS ca
JOIN course AS c ON c.id = ca.course_id
ORDER BY c.id;
-- A CTE is scoped to this statement, not a permanently stored table.
```

### Q22. Find the second-highest DISTINCT score

```sql
SELECT MAX(score) AS second_highest_score
FROM enrollment
WHERE score < (SELECT MAX(score) FROM enrollment);
-- Result: 85. Repeated 85 values count as one distinct score level.
-- Result is NULL if fewer than two distinct non-NULL scores exist.
```

### Q23. Rank scores and explain ties

```sql
SELECT learner_id, course_id, score,
       ROW_NUMBER() OVER (
           ORDER BY score DESC, learner_id, course_id
       ) AS row_number_value,
       RANK() OVER (ORDER BY score DESC) AS rank_value,
       DENSE_RANK() OVER (ORDER BY score DESC) AS dense_rank_value
FROM enrollment
WHERE score IS NOT NULL
ORDER BY score DESC, learner_id, course_id;
-- Tie-breakers belong in ROW_NUMBER when one deterministic order is needed.
-- Adding learner_id to RANK's order would change which rows count as ties.
```

| learner_id | course_id | score | ROW_NUMBER | RANK | DENSE_RANK |
| --- | --- | --- | --- | --- | --- |
| 10 | 1 | 90 | 1 | 1 | 1 |
| 10 | 2 | 85 | 2 | 2 | 2 |
| 20 | 2 | 85 | 3 | 2 | 2 |

If another row had score 80, its `RANK` would be 4 and its `DENSE_RANK` would be 3.

### Q24. Get the top score per course, including ties

```sql
WITH ranked AS (
    SELECT learner_id, course_id, score,
           DENSE_RANK() OVER (
               PARTITION BY course_id ORDER BY score DESC
           ) AS score_rank
    FROM enrollment
    WHERE score IS NOT NULL
)
SELECT learner_id, course_id, score
FROM ranked
WHERE score_rank = 1
ORDER BY course_id, learner_id;
-- Course 1: Ava, 90. Course 2: Ava AND Ben, 85.
-- Course 3 has no scored enrollment and does not appear.
```

```text
Rows → partition by course → rank within each course → keep rank 1

Course 1: Ava 90, rank 1       → keep Ava
Course 2: Ava 85, Ben 85       → both rank 1 → keep both
```

`GROUP BY` collapses rows into group summaries. Window functions keep individual rows and add calculated values. Filtering a window result requires an outer query or CTE in MySQL. [MySQL window-function reference](https://dev.mysql.com/doc/refman/8.4/en/window-functions-usage.html)

### Q25. Find each learner's latest enrollment

```sql
WITH ranked AS (
    SELECT learner_id, course_id, enrolled_on,
           ROW_NUMBER() OVER (
               PARTITION BY learner_id
               ORDER BY enrolled_on DESC, course_id DESC
           ) AS rn
    FROM enrollment
)
SELECT learner_id, course_id, enrolled_on
FROM ranked
WHERE rn = 1
ORDER BY learner_id;
-- Ava: course 2; Ben: course 2; Cara: course 3.
-- course_id breaks same-date ties deterministically.
```

### Q26. Running enrollment count by date

```sql
WITH daily AS (
    SELECT enrolled_on, COUNT(*) AS daily_count
    FROM enrollment
    GROUP BY enrolled_on
)
SELECT enrolled_on, daily_count,
       SUM(daily_count) OVER (
           ORDER BY enrolled_on
           ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS running_count
FROM daily
ORDER BY enrolled_on;
-- Running counts: 1, 2, 3, 4.
```

### Q27. UNION versus UNION ALL

```sql
SELECT learner_id FROM enrollment WHERE course_id = 1
UNION
SELECT learner_id FROM enrollment WHERE course_id = 2
ORDER BY learner_id;
-- UNION removes duplicate result rows: 10, 20.

SELECT learner_id FROM enrollment WHERE course_id = 1
UNION ALL
SELECT learner_id FROM enrollment WHERE course_id = 2
ORDER BY learner_id;
-- UNION ALL retains duplicates: 10, 10, 20.
```

Both sides need compatible columns. Removing duplicates is extra work; choose based on the required result.

### Q28. Date filtering that handles a time column too

```sql
SELECT learner_id, course_id, enrolled_on
FROM enrollment
WHERE enrolled_on >= '2026-09-01'
  AND enrolled_on <  '2026-10-01'
ORDER BY enrolled_on, learner_id, course_id;
-- Half-open interval: September start included, October start excluded.
-- This pattern also works when the column contains times.
-- Avoid assuming September 30 at midnight includes that whole day.
```

## 7. Employee interview questions

This additional table gives you salary and manager questions that a `course` table cannot naturally demonstrate.

```sql
CREATE TABLE employee (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(12,2) NOT NULL,
    manager_id INT,
    FOREIGN KEY (manager_id) REFERENCES employee(id)
);

-- Insert managers before their reports to satisfy the foreign key.
INSERT INTO employee VALUES (1, 'Maya', 'Engineering', 120000, NULL);
INSERT INTO employee VALUES (2, 'Leo', 'Engineering', 100000, 1);
INSERT INTO employee VALUES (3, 'Nia', 'Engineering', 100000, 1);
INSERT INTO employee VALUES (4, 'Omar', 'Sales', 90000, NULL);
INSERT INTO employee VALUES (5, 'Priya', 'Sales', 95000, 4);
```

| id | name | department | salary | manager_id |
| --- | --- | --- | --- | --- |
| 1 | Maya | Engineering | 120000 | NULL |
| 2 | Leo | Engineering | 100000 | 1 |
| 3 | Nia | Engineering | 100000 | 1 |
| 4 | Omar | Sales | 90000 | NULL |
| 5 | Priya | Sales | 95000 | 4 |

### Q29. Find employees earning more than their manager

```sql
SELECT e.name AS employee_name, e.salary,
       m.name AS manager_name, m.salary AS manager_salary
FROM employee AS e
JOIN employee AS m ON m.id = e.manager_id
WHERE e.salary > m.salary
ORDER BY e.id;
-- Priya: 95000 > Omar's 90000.
```

```text
Priya.manager_id = 4 → join employee again as m → Omar.id = 4
Priya.salary 95000   → compare with Omar.salary 90000 → keep Priya
```

### Q30. Find employees earning above their department average

```sql
WITH department_averages AS (
    SELECT department, AVG(salary) AS average_salary
    FROM employee
    GROUP BY department
)
SELECT e.name, e.department, e.salary, d.average_salary
FROM employee AS e
JOIN department_averages AS d ON d.department = e.department
WHERE e.salary > d.average_salary
ORDER BY e.id;
-- Maya in Engineering and Priya in Sales.
```

### Q31. Find repeated salary values

```sql
SELECT department, salary, COUNT(*) AS matching_employees
FROM employee
GROUP BY department, salary
HAVING COUNT(*) > 1
ORDER BY department, salary;
-- Engineering, 100000, 2.
-- "Duplicate" must specify which columns define duplication.
```

For repeated emails in an unconstrained import/staging table, use the same pattern with `GROUP BY email`. The practice `learner.email` is UNIQUE, so it intentionally prevents such duplicates.

### Q32. Find the top two DISTINCT salary levels per department

```sql
WITH ranked AS (
    SELECT id, name, department, salary,
           DENSE_RANK() OVER (
               PARTITION BY department ORDER BY salary DESC
           ) AS salary_rank
    FROM employee
)
SELECT name, department, salary, salary_rank
FROM ranked
WHERE salary_rank <= 2
ORDER BY department, salary DESC, id;
-- Engineering returns Maya, Leo AND Nia because ties are included.
-- ROW_NUMBER <= 2 would instead return exactly two employees per department.
```

## 8. Changing data and transactions

These demonstrations use `ROLLBACK` so the practice data remains available. Run each block as a sequence on the same connection. With normal autocommit and no explicit transaction, changes usually commit statement by statement.

### Q33. UPDATE one course

```sql
START TRANSACTION;

-- Inspect the row before changing it.
SELECT id, name, author FROM course WHERE id = 1;

UPDATE course
SET name = 'Learn AWS Fundamentals'
WHERE id = 1;

SELECT id, name, author FROM course WHERE id = 1;
ROLLBACK; -- Restore the original name for this practice exercise.
-- Use COMMIT instead when you intend to keep the transaction's changes.
```

### Q34. Delete a referenced parent and its children deliberately

```sql
START TRANSACTION;

-- Enrollment references course, so delete matching children first.
DELETE FROM enrollment WHERE course_id = 1;
DELETE FROM course WHERE id = 1;

SELECT id, name FROM course ORDER BY id; -- IDs 2, 3, 4 for now.
ROLLBACK; -- Bring the original course and enrollment back.
```

```text
course.id = 1 ← enrollment.course_id = 1
       ↓ deleting the parent first violates this foreign key
Delete matching children → delete parent → COMMIT or ROLLBACK both together
```

A foreign key's delete behavior depends on its declared rule. This schema does not use `ON DELETE CASCADE`.

### Q35. Lock a row before a read-modify-write operation

```sql
START TRANSACTION;

SELECT salary
FROM employee
WHERE id = 2
FOR UPDATE; -- Lock the selected row for a subsequent update.

UPDATE employee
SET salary = salary + 5000
WHERE id = 2;

ROLLBACK; -- Practice only; release the lock and discard the raise.
```

Keep transactions short. Locks can block other writers and concurrent transactions can deadlock. Applications need an appropriate retry policy for transient deadlock failures. For a simple increment, `SET salary = salary + 5000` already performs the update atomically; the locking read is useful when your business decision depends on inspecting the current row.

## 9. Pagination, indexes and query plans

### Q36. Offset pagination and keyset pagination

```sql
-- Skip two ordered rows, then return two rows: courses 3 and 4.
SELECT id, name
FROM course
ORDER BY id
LIMIT 2 OFFSET 2;

-- Resume after the last ID from the previous page.
SELECT id, name
FROM course
WHERE id > 2
ORDER BY id
LIMIT 2;
```

```text
Offset:  ordered rows → skip N → return page
Keyset:  last seen key → seek after that key → return page
```

Large offsets can require substantial scanning. Keyset pagination needs an ordering key and cursor; nonunique sorting requires a tie-breaker such as `(score, id)`. Neither approach automatically creates a consistent snapshot across separate requests.

### Q37. Create a useful composite index and inspect the plan

```sql
-- Run once. Do not repeat CREATE INDEX with the same name.
CREATE INDEX idx_enrollment_course_date
ON enrollment (course_id, enrolled_on);

EXPLAIN
SELECT learner_id, enrolled_on
FROM enrollment
WHERE course_id = 2
  AND enrolled_on >= '2026-09-01'
ORDER BY enrolled_on;
-- EXPLAIN reports the optimizer's plan, not the actual returned rows.
```

```text
Index starts with course_id → narrow to one course
                           → ordered enrolled_on values → scan a date range
```

An index can speed reads but costs storage and write maintenance. A `(course_id, enrolled_on)` index generally serves predicates beginning with `course_id`; it is not interchangeable with `(enrolled_on, course_id)`. For this tiny dataset, the optimizer may reasonably choose a table scan. `EXPLAIN ANALYZE` is available in MySQL 8.0.18+ and actually executes the query to report observed work.

## 10. Logical query order

This is a useful reasoning model, not a promise about the optimizer's physical execution steps:

```text
FROM / JOIN
    ↓ build candidate rows
WHERE
    ↓ remove individual rows
GROUP BY
    ↓ form groups
HAVING
    ↓ remove groups
Window calculations
    ↓ compute values across surviving rows/groups
SELECT / DISTINCT
    ↓ produce the requested output
ORDER BY
    ↓ sort
LIMIT / OFFSET
    ↓ choose the returned page
```

Consequences: a `SELECT` alias is generally unavailable in `WHERE`; window results cannot be filtered in that same query's `WHERE`; no `ORDER BY` means no guaranteed result order.

## 11. Spring Boot interview connections

| Java/Spring concept | SQL connection | Interview explanation |
| --- | --- | --- |
| JPA entity | Table mapping | Java objects map to relational data; inspect the actual SQL |
| `@Id` | Primary key | A stable unique identifier distinguishes rows |
| `@ManyToOne` | Foreign key on the many side | Many enrollment rows can reference one course |
| Repository query | SELECT with filters/joins | Understand generated SQL and its query plan |
| `@Transactional` | Transaction boundary | Related changes commit or roll back together according to configured rules |
| DTO/report projection | Selected columns and aggregates | Fetch the data the API needs and keep entity boundaries deliberate |
| N+1 queries | Many extra SELECTs | Inspect query count and choose fetching or projections deliberately |
| Pagination | ORDER BY plus page/cursor | Stable ordering and suitable indexes matter |
| Prepared statement | SQL plus bound parameters | Bind values instead of concatenating user input into SQL |

```java
// JDBC-style example: the value is bound separately from SQL text.
PreparedStatement statement = connection.prepareStatement(
    "SELECT id, name, author FROM course WHERE id = ?");
statement.setInt(1, courseId);
// In complete code, close the statement and ResultSet appropriately.
```

Parameters bind values, not arbitrary table names or sort expressions. Allowlist dynamic identifiers. JPA JPQL uses entity and attribute names; native SQL uses table and column names.

## 12. Traps to explain aloud

| Trap | Correct reasoning |
| --- | --- |
| `column = NULL` | Use `IS NULL`; NULL participates in three-valued logic |
| `COUNT(*)` after a LEFT JOIN | It counts the unmatched placeholder row; count a nonnullable matched column |
| Filtering the right table in WHERE | It can discard unmatched LEFT JOIN rows |
| Repeated parent rows after a join | One-to-many joins multiply rows; decide whether you need rows, groups or EXISTS |
| `MAX(score), learner_id` without a valid grouping rule | A maximum does not identify the row that supplied it; rank or join to the maximum |
| Second row versus second-highest distinct value | Decide how ties should work before writing the query |
| Unordered LIMIT | It does not define which rows belong to the page |
| `SELECT *` everywhere | Unneeded columns increase coupling and transferred data |
| A transaction guarantees no concurrency problems | Isolation level and locking determine which anomalies remain possible |
| `DELETE`, `TRUNCATE`, `DROP` are interchangeable | DELETE removes rows with optional filtering; TRUNCATE empties a table; DROP removes the table definition |

In MySQL, `TRUNCATE` and many DDL statements cause implicit commits; do not assume `ROLLBACK` restores them. Constraints also govern whether these statements are allowed. ACID means atomicity, consistency of enforced invariants, isolation and durability; it does not guarantee your application's business rules are correct.

## 13. Closed-book readiness checklist

| Priority | You should write and explain from memory |
| --- | --- |
| First | INSERT, SELECT, UPDATE, DELETE, WHERE, ORDER BY, LIMIT, NULL handling |
| Next | INNER/LEFT JOIN, foreign keys, COUNT, SUM/AVG, GROUP BY, HAVING |
| Interview practice | EXISTS/NOT EXISTS, CTEs, second-highest value, top-N with ties, self join |
| Senior discussion | Transactions, isolation, deadlocks, indexes, EXPLAIN, pagination, N+1, parameter binding |

Timed exercise, about 30–40 minutes:

1. Find courses by author, sorted by name.
2. List every course with its enrollment count, including zero.
3. Find learners without an enrollment.
4. Find the second-highest distinct score.
5. Find the top score per course, preserving ties.
6. Find employees earning more than their manager.
7. Update a row within a transaction and roll it back.
8. Propose an index for a filtered, sorted query and explain its write cost.

For each answer: state the expected columns, explain NULLs and ties, predict the result on the sample data, and name a useful edge case. These queries provide a strong practice baseline; your actual interview may also ask schema design, normalization, SQL dialect differences or database-specific performance questions.

## References

- [MySQL JOIN syntax](https://dev.mysql.com/doc/refman/8.4/en/join.html)
- [MySQL GROUP BY handling](https://dev.mysql.com/doc/refman/8.4/en/group-by-handling.html)
- [MySQL window functions](https://dev.mysql.com/doc/refman/8.4/en/window-functions-usage.html)
- [MySQL transactions](https://dev.mysql.com/doc/refman/8.4/en/commit.html)
- [MySQL CHECK constraints](https://dev.mysql.com/doc/refman/8.4/en/create-table-check-constraints.html)
- [MySQL EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/explain.html)
