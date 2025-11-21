-- =============================================================================
-- Migration V3: Convert Project-TestSuite Relationship
-- =============================================================================
-- Converts Many-to-Many to Many-to-One (TestSuite → Project)
-- Each test suite now belongs to exactly one project
-- =============================================================================

-- Step 1: Add project_id column to test_suites table
ALTER TABLE test_suites
ADD COLUMN IF NOT EXISTS project_id BIGINT;

-- Step 2: Migrate data from join table to foreign key
-- For test suites linked to multiple projects, this picks the first project
-- You may want to customize this logic based on business requirements
UPDATE test_suites ts
SET project_id = (
    SELECT pts.project_id
    FROM project_test_suites pts
    WHERE pts.test_suite_id = ts.id
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 FROM project_test_suites pts WHERE pts.test_suite_id = ts.id
);

-- Step 3: Handle test suites that were linked to multiple projects (if any)
-- This creates duplicate test suites - one for each project relationship
-- IMPORTANT: This section may need to be customized based on business requirements
-- Comment out if you want to handle this manually

-- Insert duplicates for test suites with multiple project associations
INSERT INTO test_suites (name, description, variables, project_id, created_at, updated_at)
SELECT
    ts.name,
    ts.description,
    ts.variables,
    pts.project_id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM test_suites ts
JOIN project_test_suites pts ON pts.test_suite_id = ts.id
WHERE ts.project_id IS NOT NULL
  AND ts.project_id != pts.project_id;

-- Step 4: Make project_id NOT NULL (after ensuring all test suites have a project)
ALTER TABLE test_suites
ALTER COLUMN project_id SET NOT NULL;

-- Step 5: Add foreign key constraint
ALTER TABLE test_suites
ADD CONSTRAINT IF NOT EXISTS fk_test_suite_project
FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- Step 6: Create index for performance
CREATE INDEX IF NOT EXISTS idx_test_suites_project_id ON test_suites(project_id);

-- =============================================================================

-- Step 7: Add endpoint_id column to test_suites table
ALTER TABLE test_suites
ADD COLUMN IF NOT EXISTS endpoint_id BIGINT;

-- Step 8: Add foreign key constraint for endpoint (optional, can be NULL)
ALTER TABLE test_suites
ADD CONSTRAINT IF NOT EXISTS fk_test_suite_endpoint
FOREIGN KEY (endpoint_id) REFERENCES endpoints(id) ON DELETE SET NULL;

-- Step 9: Create index for performance
CREATE INDEX IF NOT EXISTS idx_test_suites_endpoint_id ON test_suites(endpoint_id);

-- =============================================================================

-- Step 10: Drop the old many-to-many join table
-- IMPORTANT: Only do this after verifying the migration was successful
DROP TABLE IF EXISTS project_test_suites;

-- =============================================================================
-- Migration complete
-- =============================================================================

-- NOTES FOR MANUAL REVIEW:
-- 1. If you have test suites that were shared across multiple projects,
--    this migration creates duplicates (one per project).
-- 2. Test cases within duplicated test suites are NOT duplicated by this script.
--    You may need additional logic to handle test case duplication if needed.
-- 3. Review the data after migration to ensure correctness.
-- 4. Consider running this migration in a transaction and testing on a backup first.
