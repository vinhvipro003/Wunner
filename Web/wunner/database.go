package wunner

import (
	"fmt"
	"regexp"
	"time"

	"gomessenger/database"

	"github.com/apex/log"
)

type (
	User struct {
		UserName     string   `dynamo:"userName,hash"`
		UserPassword string   `dynamo:"userPassword"`
		UserFullname string   `dynamo:"userFullname"`
		UserDoB      string   `dynamo:"userDoB"`
		UserAddress  string   `dynamo:"userAddress"`
		UserType     string   `dynamo:"userType"`
		TeamID       []string `dynamo:"teamID"`
		LayoutType   string   `dynamo:"layoutType"`
		StationID    string   `dynamo:"stationID"`
	}

	Team struct {
		TeamID    string  `dynamo:"teamID,hash"`
		TeamName  string  `dynamo:"teamName"`
		EventID   string  `dynamo:"eventID"`
		TeamPoint []Point `dynamo:"teamPoint"`
	}

	Point struct {
		StationID string `dynamo:"stationID"`
		Point     int    `dynamo:"point"`
		TimeStart int64  `dynamo:"timeStart"`
		TimeEnd   int64  `dynamo:"timeEnd"`
	}

	Event struct {
		EventID          string   `dynamo:"eventID,hash"`
		EventName        string   `dynamo:"eventName"`
		EventPlace       string   `dynamo:"eventPlace"`
		EventDate        string   `dynamo:"eventDate"`
		EventDescription string   `dynamo:"eventDescription"`
		EventPicture     string   `dynamo:"eventPicture"`
		StationID        []string `dynamo:"stationID"`
	}

	Station struct {
		StationID          string `dynamo:"stationID,hash"`
		StationName        string `dynamo:"stationName"`
		StationPoint       int    `dynamo:"stationPoint"`
		StationTime        int64  `dynamo:"stationTime"`
		StationRun         int64  `dynamo:"stationRun"`
		StationDate        string `dynamo:"stationDate"`
		StationDescription string `dynamo:"stationDescription"`
		StationIndex       int    `dynamo:"stationIndex"`
		StationLaw         string `dynamo:"stationLaw"`
		StationLatitude    string `dynamo:"stationLatitude"`
		StationLongtitude  string `dynamo:"stationLongtitude"`
	}
)

const (
	AWSRegion    = "us-east-1"
	UserTable    = "Wunner_User"
	TeamTable    = "Wunner_Team"
	StationTable = "Wunner_Station"
	EventTable   = "Wunner_Event"
)

/*************************************** User ***********************************************/
func createUserTable() bool {
	err := database.CreateNewTable(AWSRegion, UserTable, User{})
	if err != nil {
		log.Errorf("createUserTable: %s", err.Error())
		return false
	}
	return true
}

func AddNewUser(user *User) bool {
	err := database.AddNewObject(AWSRegion, UserTable, user)
	if err != nil {
		log.Errorf("addNewUser: %s", err.Error())
		return false
	}
	return true
}

func GetUserInfo(userName string) (*User, bool) {
	ok := true
	var user User
	err := database.GetObject(AWSRegion, UserTable, "userName", userName, &user)

	if err != nil {
		log.Errorf("getUserInfo: %s", err.Error())
		ok = false
	}
	return &user, ok
}

func getListUserInfo() []User {
	var users []User
	err := database.SearchAllObjectNonExp(AWSRegion, UserTable, &users)
	if err != nil {
		log.Errorf("GetListUserInfo: %s", err.Error())
		return users
	}
	return users
}

func UpdateUserInfo(userName string, fields map[string]interface{}) bool {
	ok := true
	err := database.UpdateObject(AWSRegion, UserTable, "userName", userName, fields)
	if err != nil {
		log.Errorf("updateUserInfo: %s", err.Error())
		ok = false
	}
	return ok
}

/*************************************** Team ***********************************************/
func createTeamTable() bool {
	err := database.CreateNewTable(AWSRegion, TeamTable, Team{})
	if err != nil {
		log.Errorf("createTeamTable: %s", err.Error())
		return false
	}
	return true
}

func AddNewTeam(team *Team) bool {
	err := database.AddNewObject(AWSRegion, TeamTable, team)
	if err != nil {
		log.Error("addNewTeam: " + err.Error())
		return false
	}
	return true
}

func GetTeamInfo(teamID string) (*Team, bool) {
	ok := true
	var team Team
	err := database.GetObject(AWSRegion, TeamTable, "teamID", teamID, &team)

	if err != nil {
		log.Error("getTeamInfo" + err.Error())
		ok = false
	}
	return &team, ok
}

func UpdateTeamInfo(teamID string, fields map[string]interface{}) bool {
	ok := true
	err := database.UpdateObject(AWSRegion, TeamTable, "teamID", teamID, fields)
	if err != nil {
		log.Error("updateTeamInfo: " + err.Error())
		ok = false
	}
	return ok
}

func deleteTeam(teamID string) bool {
	err := database.DeleteObject(AWSRegion, TeamTable, "teamID", teamID)
	if err != nil {
		log.Error("deleteTeam" + err.Error())
		return false
	}
	return true
}

/*************************************** Station ***********************************************/
func createStationTable() bool {
	err := database.CreateNewTable(AWSRegion, StationTable, Station{})
	if err != nil {
		log.Errorf("createStationTable: %s", err.Error())
		return false
	}
	return true
}

func addNewStation(station *Station) bool {
	err := database.AddNewObject(AWSRegion, StationTable, station)
	if err != nil {
		log.Error("addNewStation: " + err.Error())
		return false
	}
	return true
}

func GetStationInfo(stationID string) (*Station, bool) {
	ok := true
	var station Station
	err := database.GetObject(AWSRegion, StationTable, "stationID", stationID, &station)

	if err != nil {
		log.Error("getStationInfo" + err.Error())
		ok = false
	}
	return &station, ok
}

func updateStation(stationID, mID string) bool {
	ok := true
	var fields = make(map[string]interface{})
	fields["mID"] = mID
	fields["expiredTime"] = time.Now().Unix() + 60
	err := database.UpdateObject(AWSRegion, StationTable, "stationID", stationID, fields)
	if err != nil {
		log.Error("updateStation: " + err.Error())
		ok = false
	}
	return ok
}

func deleteStation(stationID string) bool {
	err := database.DeleteObject(AWSRegion, StationTable, "stationID", stationID)
	if err != nil {
		log.Error("deleteStation" + err.Error())
		return false
	}
	return true
}

/*************************************** Event ***********************************************/
func createEventTable() bool {
	err := database.CreateNewTable(AWSRegion, EventTable, Event{})
	if err != nil {
		log.Errorf("createEventTable: %s", err.Error())
		return false
	}
	return true
}

func AddNewEvent(event *Event) bool {
	err := database.AddNewObject(AWSRegion, EventTable, event)
	if err != nil {
		log.Errorf("addNewEvent: %s", err.Error())
		return false
	}
	return true
}

func GetEventInfo(eventID string) (*Event, bool) {
	ok := true
	var event Event
	err := database.GetObject(AWSRegion, EventTable, "eventID", eventID, &event)

	if err != nil {
		log.Errorf("getEventInfo: %s", err.Error())
		ok = false
	}
	return &event, ok
}

func GetListEventInfo() []Event {
	var events []Event
	err := database.SearchAllObjectNonExp(AWSRegion, EventTable, &events)
	if err != nil {
		log.Errorf("getListUserInfo: %s", err.Error())
		return events
	}
	return events
}

func updateEventInfo(eventID string, fields map[string]interface{}) bool {
	ok := true
	err := database.UpdateObject(AWSRegion, EventTable, "eventID", eventID, fields)
	if err != nil {
		log.Errorf("updateEventInfo: %s", err.Error())
		ok = false
	}
	return ok
}

/*************************************** Misc ***********************************************/
func GetCurrentTimestamp(timezone int64) int64 {
	return time.Now().Unix() + timezone*60*60
}

func getMidnightTimestamp(date string) int64 {
	test, err := time.Parse(time.RFC3339, date+"T00:00:00+00:00")
	if err != nil {
		return 0
	}

	return test.Unix()
}

func getCurrentMidnightTimestamp(timezone int) int64 {
	var curTime = time.Now().Add(time.Hour * time.Duration(timezone))
	test, err := time.Parse(time.RFC3339, fmt.Sprintf("%d-%02d-%02dT00:00:00+00:00", curTime.Year(), curTime.Month(), curTime.Day()))
	if err != nil {
		log.Errorf("getCurrentMidnightTimestamp: %s", err.Error())
		return 0
	}

	return test.Unix()
}

func getDateTime(timestamp int64) string {
	if timestamp == 0 {
		return ""
	}
	datetime := time.Unix(timestamp, 0)
	// dateStr := datetime.Format(time.RFC3339)
	return fmt.Sprintf("%02d-%02d-%d %02d:%02d", datetime.Day(), int(datetime.Month()), datetime.Year(), datetime.Hour(), datetime.Minute())
}

func verifyPhoneNumber(number string) bool {
	check, _ := regexp.MatchString("^0[0-9]{9,10}$", number)
	return check
}

func verifyEmail(email string) bool {
	check, _ := regexp.MatchString("^([\\w\\.\\_]+)@([\\w\\.\\_]+).([a-z]{2,})$", email)
	return check
}

func verifyDate(date string) bool {
	check, _ := regexp.MatchString("^[0-3][0-9]-[0-9][0-9]-[2-9][0-9][0-9][0-9]$", date)
	return check
}

func verifyTime(time string) bool {
	check, _ := regexp.MatchString("^[0-1][3-9]:00$", time)
	return check
}

func getBookingDate(timestamp int64) string {
	if timestamp == 0 {
		return ""
	}
	datetime := time.Unix(timestamp, 0)
	// dateStr := datetime.Format(time.RFC3339)
	return fmt.Sprintf("%d-%02d-%02d", datetime.Year(), int(datetime.Month()), datetime.Day())
}
func GetTimestamp(date string) int64 {
	test, err := time.Parse(time.RFC3339, date+"T00:00:00+00:00")
	if err != nil {
		fmt.Println(err)
		return 0
	}

	return test.Unix()
}
