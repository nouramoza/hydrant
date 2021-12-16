## Hydrant Application


### Problem Description

Lots of fire hydrants cover the entire area of New York City. 
In case of a fire, a fire brigade arrives with N trucks and connects fire hoses to the N nearest hydrants in the fire area. 
You need to calculate the total length of fire hoses required to extinguish a fire.


### Assumptions

- There is limitation in maximum number of Trucks
- There is limitation in Maximum length of fire hose
- I developed the application in 2 ways,
	- I retrieve all nearest data by a query, all the conditions and sort of data is done at dataset side.
			in this case, I use Latitude and Longitude to measure distance to the Fire.
	- Retrieve all the hydrants in the distance of less than 1000(max length of a fire hose), I used function distance_in_meters() of SoQl, in this function I send the_geom coordinates, and in the code it will find each hydrant distance, sort them by distance to the fire and returns the result.
- A fire hose connected to one hydrant is always a straight line between the hydrant's and the fire's location.
All the distances are calculated as following formula:
		√(〖hydrantLat-fireLat〗^2-〖hydrantLong-fireLong〗^2 )
		

### Tips
- You can find Hydrants API documentation from here => https://dev.socrata.com/foundry/data.cityofnewyork.us/5bgh-vtsn
- The direct call to API is `https://data.cityofnewyork.us/resource/5bgh-vtsn.json`


### Input

You have to expose an API endpoint accepting the parameters: 
- Coordinates of the fire (any format you prefer)
- Number of fire trucks N

	#### Example Input
	As we have 2 ways of developing and calculation the distances, we have 2 APIs and each one has its own input json
	```input json example for codeBase function
	{
		"theGeom":{
			"type":"point",
			"coordinates":[-73.78156804377382,40.7422177771488]
		},
		"numberOfFireTrucks":3
	}   
	```
	```input json example for queryBase function
	{
        "theGeom":{
        "type":"point",
        "coordinates":[-73.78156804377382,40.7422177771488]
        },
        "numberOfFireTrucks":3
    }     
	```

### Output

The API endpoint should return a JSON object containing:
- total firehoses length in meters
- list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
   
	#### Example outcome from the API
	 
	```output json example
	{
	  "totalFirehosesLength": 485.52237958215517,
	  "hydrants": [
		{
		  "unitId": "H415472",
		  "distanceToFire": 161.83913805999595
		},
		{
		  "unitId": "H415833",
		  "distanceToFire": 161.83942225079014
		},
		{
		  "unitId": "H415678",
		  "distanceToFire": 161.84381927136909
		}
	  ]
	}
	```

### Run Application
- Run DummyTest file to see the test cases results
- Run the application and open
http://localhost:8040/swagger-ui.html
to test the APIs with swagger.

some input examples are:

{
    "latitude":"41.8822168",	 
    "longitude" : "-73.59157092",
    "numberOfFireTrucks":3
}  







