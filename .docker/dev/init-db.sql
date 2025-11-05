-- Database initialization script for FF14 Static Maker
-- This script runs when the database is first created

USE fflog;

-- Initialize region statistics
-- The tables will be created automatically by Hibernate
-- This just ensures we have initial data for all regions

-- Note: The actual tables (players, raid_groups, region_stats) will be created
-- by Spring Boot JPA with ddl-auto=update when the application starts

-- You can add seed data here if needed
-- For example:
-- INSERT INTO region_stats (region, statics, players) VALUES
--     ('EUROPE', 0, 0),
--     ('AMERICA', 0, 0),
--     ('JAPAN', 0, 0)
-- ON DUPLICATE KEY UPDATE statics=statics;
