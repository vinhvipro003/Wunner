package wunner

func Init() {
	createEventTable()
	createStationTable()
	createTeamTable()
	createUserTable()
}

func CheckLogin(user User) (*User, bool) {
	userCheck, ok := getUserInfo(user.UserName)
	if user.UserPassword != userCheck.UserPassword {
		ok = false
	}
	return userCheck, ok
}

func GetStation(stationID string) (*Station, bool) {
	station, ok := getStationInfo(stationID)
	return station, ok
}

func StartStation(stationID string) (*Station, bool) {
	station, ok := getStationInfo(stationID)
	return station, ok
}

func FinishStation(stationID string) (*Station, bool) {
	station, ok := getStationInfo(stationID)
	return station, ok
}

func MarkStation(teamID, stationID string, point int) bool {
	team, ok := GetTeamInfo(teamID)
	if ok {
		for i := range team.TeamPoint {
			if team.TeamPoint[i].StationID == stationID {
				team.TeamPoint[i].Point = point
				var fields = make(map[string]interface{})
				fields["teamPoint"] = team.TeamPoint
				UpdateTeam(teamID, fields)
				return ok
			}
		}
		ok = false
	}
	return ok
}
