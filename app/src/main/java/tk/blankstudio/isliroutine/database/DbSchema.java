package tk.blankstudio.isliroutine.database;

/**
 * Created by deadsec on 11/8/17.
 * Database schema of different tables
 * and their column properties
 */


public class DbSchema {

    public static final class Course {
        public static final String NAME = "all_course";
        public static final class Cols {
            public static final String ID = "uid";
            public static final String TITLE = "title";
            public static final String MODULE_ID = "module_id";
            public static final String MODULE_LEADER = "module_leader";
            public static final String ABOUT = "about";
            public static final String RESOURCES = "resources";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
        }
    }

    public static final class Lession {
        public static final String NAME = "lession";
        public static final class Cols {
            public static final String TYPE = "type";
            public static final String ID = "uid";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
        }
    }

    public static final class Room {
        public static final String NAME = "room";
        public static final class Cols {
            public static final String ID = "uid";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String BLOCK = "block";
            public static final String CLASSROOM = "class_room";
        }
    }

    public static final class Teacher {
        public static final String NAME = "teacher";
        public static final class Cols {
            public static final String ID = "uid";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String NAME = "name";
            public static final String OFFICE_HOUR = "office_hour";
            public static final String PHONE = "phone";
            public static final String EMAIL = "email";
            public static final String WEBSITE = "website";
            public static final String QUALIFICATION = "qualification";
            public static final String EXPERIENCE = "experience";
            public static final String MISC = "misc";
        }
    }

    public static final class TimeTable {
        public static final String NAME = "timetable";
        public static final class Cols {
            public static final String ID = "uid";
            public static final String CREATED_AT= "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String ROOM_ID = "room_id";
            public static final String LESSION_ID = "lession_id";
            public static final String TEACHER_ID = "teacher_id";
            public static final String YEAR_GROUP_ID = "year_group_id";
            public static final String COURSE_ID = "course_id";
            public static final String START_HOUR = "start_hour";
            public static final String END_HOUR = "end_hour";
            public static final String START_MINUTE = "start_minute";
            public static final String END_MINUTE = "end_minute";
            public static final String DAYS = "days";

        }
    }

    public static final class YearGroup {
        public static final String NAME = "year_group";
        public static final class Cols {
            public static final String ID = "uid";
            public static final String CREATED_AT = "created_at";
            public static final String UPDATED_AT = "updated_at";
            public static final String START_DAY = "start_day";
            public static final String YEAR = "year";
            public static final String END_DAY = "end_day";
            public static final String START_YEAR = "start_year";
            public static final String END_YEAR = "end_year";
            public static final String START_MONTH = "start_month";
            public static final String END_MONTH = "end_month";
            public static final String GROUP = "group_name";
        }
    }

    public static final class ClassRoomCourse {
        public static final String NAME="class_room_course";
        public static final class Cols {
            public static final String NAME="name";
            public static final String ID="id";
            public static final String COURSE_STATE="course_state";
            public static final String DESCRIPTION="description";
            public static final String ENROLLMENT_CODE="enrollment_code";
            public static final String SECTION="section";
            public static final String DESCRIPTIONHEADING="descriptionHeading";
            public static final String ALTERNATE_LINK="alternate_link";
            public static final String GOOGLE_DRIVE_LINK="google_drive_link";


        }


    }

}
