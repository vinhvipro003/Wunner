package main

import (
	"Wunner/wunner"
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"

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

	team := router.Group("/team")
	{
		team.GET("/get", getTeam())
		team.GET("/submit", submit())
	}

	event := router.Group("/event")
	{
		event.GET("/get", getEvent())
		event.POST("/add", addEvent())
		event.POST("/attend", attendEvent())
	}

	station := router.Group("/station")
	{
		station.GET("/get", getStation())
		station.POST("/mark", markStation())
	}

	addr := ":" + "8080" //os.Getenv("PORT")
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

		userCheck, ok := wunner.CheckLogin(user)
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

func submit() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		timeEnd := c.Query("timeEnd")
		var fields = make(map[string]interface{})
		fields["timeEnd"] = timeEnd
		wunner.UpdateTeam(teamID, fields)
		c.JSON(http.StatusOK, "Submited")
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

		station, ok := wunner.GetStation(stationID)
		if ok {
			c.JSON(http.StatusOK, station)
		} else {
			c.String(http.StatusNotFound, "Not found stationID")
		}
	}
}

func startStation() gin.HandlerFunc {
	return func(c *gin.Context) {
		stationID := c.Query("stationID")
		//teamID := c.Query("teamID")

		station, ok := wunner.GetStation(stationID)
		if ok {
			c.JSON(http.StatusOK, station)
		} else {
			c.String(http.StatusNotFound, "Not found stationID")
		}
	}
}

func finishStation() gin.HandlerFunc {
	return func(c *gin.Context) {
		stationID := c.Query("stationID")
		//teamID := c.Query("teamID")

		station, ok := wunner.FinishStation(stationID)
		if ok {
			c.JSON(http.StatusOK, station)
		} else {
			c.String(http.StatusNotFound, "Not found stationID")
		}
	}
}

func markStation() gin.HandlerFunc {
	return func(c *gin.Context) {
		teamID := c.Query("teamID")
		stationID := c.Query("stationID")
		point := c.Query("point")
		pointInt, _ := strconv.Atoi(point)
		wunner.MarkStation(teamID, stationID, pointInt)
		c.String(http.StatusOK, "Got your request")
	}
}
