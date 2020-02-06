package main

import (
	"Wunner/wunner"
	"encoding/json"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	cors "github.com/itsjamie/gin-cors"

	"github.com/apex/log"
)

func main() {
	//wunner.Init()
	router := gin.Default()
	user := router.Group("/user")
	{
		user.POST("/login", login())
		user.POST("/register", register())
		user.GET("/setlayout", setLayout())
	}

	router.Use(cors.Middleware(cors.Config{
		Origins:         "*",
		Methods:         "GET, PUT, POST, DELETE",
		RequestHeaders:  "Origin, Authorization, Content-Type",
		ExposedHeaders:  "",
		MaxAge:          50 * time.Second,
		Credentials:     true,
		ValidateHeaders: false,
	}))

	time := router.Group("/time")
	{
		time.GET("/countdown", getCountDownTime())
	}

	team := router.Group("/team")
	{
		team.GET("/get", getTeam())
		team.POST("/add", addTeam())
		team.GET("/start", startMission())
		team.GET("/submit", submit())
	}

	event := router.Group("/event")
	{
		event.GET("/get", getEvent())
		event.GET("/getall", getAllEvent())
		event.POST("/add", addEvent())
		event.POST("/attend", attendEvent())
	}

	station := router.Group("/station")
	{
		station.GET("/get", getStation())
		station.POST("/mark", markStation())
		station.GET("/getwithusername", getStationWithUserName())
	}

	addr := ":" + os.Getenv("PORT")
	router.Run(addr)
}

/*************************************** User ***********************************************/
func login() gin.HandlerFunc {
	return func(c *gin.Context) {
		var user wunner.User
		defer c.Request.Body.Close()
		if err := json.NewDecoder(c.Request.Body).Decode(&user); err != nil {
			log.Errorf("json.Unmarshall payload got error: %s", err.Error())
			c.String(http.StatusBadRequest, "Invalid request")
			return
		}

		userCheck, ok := wunner.GetUserInfo(user.UserName)
		if user.UserPassword != userCheck.UserPassword {
			ok = false
		}

		if ok {
			c.JSON(http.StatusOK, userCheck)
		} else {
			c.String(http.StatusUnauthorized, "Wrong username or password")
		}
	}
}

func register() gin.HandlerFunc {
	return func(c *gin.Context) {
		var user wunner.User
		defer c.Request.Body.Close()
		if err := json.NewDecoder(c.Request.Body).Decode(&user); err != nil {
			log.Errorf("json.Unmarshall payload got error: %s", err.Error())
			c.String(http.StatusBadRequest, "Invalid request")
			return
		}

		ok := wunner.AddNewUser(&user)
		if ok {
			c.JSON(http.StatusOK, "Added new user")
		} else {
			c.String(http.StatusUnauthorized, "Wrong username or password")
		}
	}
}

func setLayout() gin.HandlerFunc {
	return func(c *gin.Context) {
		userName := c.Query("userName")
		layoutType := c.Query("layoutType")
		var fields = make(map[string]interface{})
		fields["layoutType"] = layoutType
		wunner.UpdateUserInfo(userName, fields)
		c.JSON(http.StatusOK, "Updated layout type")
	}
}

/*************************************** Team ***********************************************/
func getTeam() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")

		team, ok := wunner.GetTeamInfo(teamID)
		if ok {
			c.JSON(http.StatusOK, team)
		} else {
			c.String(http.StatusNotFound, "Not found teamID")
		}
	}
}

func addTeam() gin.HandlerFunc {
	return func(c *gin.Context) {
		var team wunner.Team
		defer c.Request.Body.Close()
		if err := json.NewDecoder(c.Request.Body).Decode(&team); err != nil {
			log.Errorf("json.Unmarshall payload got error: %s", err.Error())
			c.String(http.StatusBadRequest, "Invalid request")
			return
		}

		ok := wunner.AddNewTeam(&team)
		if ok {
			c.JSON(http.StatusOK, "Added new team")
		} else {
			c.String(http.StatusUnauthorized, "Wrong username or password")
		}
	}
}

func startMission() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		stationID := c.Query("stationID")
		timeStart := wunner.GetCurrentTimestamp(7)

		team, _ := wunner.GetTeamInfo(teamID)
		_, ok := wunner.GetStationInfo(stationID)
		if !ok {
			c.String(http.StatusNotFound, "Not found stationID")
		}

		team.TeamPoint = append(team.TeamPoint, wunner.Point{
			StationID: stationID,
			TimeStart: timeStart,
		})

		var fields = make(map[string]interface{})
		fields["teamPoint"] = team.TeamPoint
		wunner.UpdateTeamInfo(teamID, fields)
	}
}

func submit() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		stationID := c.Query("stationID")
		timeEnd := wunner.GetCurrentTimestamp(7)
		team, _ := wunner.GetTeamInfo(teamID)
		for i := range team.TeamPoint {
			if team.TeamPoint[i].StationID == stationID {
				team.TeamPoint[i].TimeEnd = timeEnd
				var fields = make(map[string]interface{})
				fields["teamPoint"] = team.TeamPoint
				wunner.UpdateTeamInfo(teamID, fields)
				break
			}
		}
		c.JSON(http.StatusOK, "Submited")
	}
}

func markStation() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		stationID := c.Query("stationID")
		point := c.Query("point")
		pointInt, _ := strconv.Atoi(point)

		team, ok := wunner.GetTeamInfo(teamID)
		if ok {
			for i := range team.TeamPoint {
				if team.TeamPoint[i].StationID == stationID {
					team.TeamPoint[i].Point = pointInt
					var fields = make(map[string]interface{})
					fields["teamPoint"] = team.TeamPoint
					wunner.UpdateTeamInfo(teamID, fields)
				}
			}
			c.String(http.StatusOK, "Got your request")
		}
	}
}

/*************************************** Time ***********************************************/
func getCountDownTime() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		stationID := c.Query("stationID")

		team, _ := wunner.GetTeamInfo(teamID)
		station, _ := wunner.GetStationInfo(stationID)
		var countDownTime int64
		countDownTime = 0
		var find = false
		for i := range team.TeamPoint {
			if team.TeamPoint[i].StationID == stationID {
				countDownTime = station.StationTime - (wunner.GetCurrentTimestamp(7) - team.TeamPoint[i].TimeStart)
			}
		}
		if find {
			c.JSON(http.StatusOK, countDownTime)
		} else {
			c.String(http.StatusNotFound, "Not receive misstion")
		}
	}
}

/*************************************** Event ***********************************************/
func addEvent() gin.HandlerFunc {
	return func(c *gin.Context) {
		var event wunner.Event
		defer c.Request.Body.Close()
		if err := json.NewDecoder(c.Request.Body).Decode(&event); err != nil {
			log.Errorf("json.Unmarshall payload got error: %s", err.Error())
			c.String(http.StatusBadRequest, "Invalid request")
			return
		}

		ok := wunner.AddNewEvent(&event)
		if ok {
			c.JSON(http.StatusOK, "Added new event")
		} else {
			c.String(http.StatusUnauthorized, "Wrong username or password")
		}
	}
}

func getEvent() gin.HandlerFunc {
	return func(c *gin.Context) {
		eventID := c.Query("eventID")

		event, ok := wunner.GetEventInfo(eventID)
		if ok {
			c.JSON(http.StatusOK, event)
		} else {
			c.String(http.StatusNotFound, "Not found eventID")
		}
	}
}

func getAllEvent() gin.HandlerFunc {
	return func(c *gin.Context) {
		events := wunner.GetListEventInfo()
		c.JSON(http.StatusOK, events)
	}
}

func attendEvent() gin.HandlerFunc {
	return func(c *gin.Context) {
		var event wunner.Event
		defer c.Request.Body.Close()
		if err := json.NewDecoder(c.Request.Body).Decode(&event); err != nil {
			log.Errorf("json.Unmarshall payload got error: %s", err.Error())
			c.String(http.StatusBadRequest, "Invalid request")
			return
		}

		c.String(http.StatusOK, "Got your request")
	}
}

/*************************************** Station ***********************************************/
func getStation() gin.HandlerFunc {
	return func(c *gin.Context) {
		stationID := c.Query("stationID")

		station, ok := wunner.GetStationInfo(stationID)
		if ok {
			c.JSON(http.StatusOK, station)
		} else {
			c.String(http.StatusNotFound, "Not found stationID")
		}
	}
}

func getStationWithUserName() gin.HandlerFunc {
	return func(c *gin.Context) {
		userName := c.Query("userName")
		user, ok := wunner.GetUserInfo(userName)
		if ok {
			var stations = []string{}
			for i := range user.TeamID {
				team, _ := wunner.GetTeamInfo(user.TeamID[i])
				event, _ := wunner.GetEventInfo(team.EventID)
				stations = event.StationID
			}
			c.JSON(http.StatusOK, stations)
		} else {
			c.String(http.StatusNotFound, "Not found userName")
		}
	}
}
