# Coding Task

### General guidelines

If you have any questions about the task, reach out to your HR contact with the questions.

When your task is completed, please do not publish it publicly online, but rather zip it and share with it your HR contact person via e-mail.

### Task

Lots of fire hydrants cover the entire area of New York City. 
In case of a fire, a fire brigade arrives with N trucks and connects firehoses to the N nearest hydrants in the fire area. 
You need to calculate the total length of firehoses required to extinguish a fire.


#### Assumptions

For simplicity, we assume that a firehose connected to one hydrant is always a straight line between the hydrant's and the fire's location.
    
#### Tips

- You can find Hydrants API documentation from here => https://dev.socrata.com/foundry/data.cityofnewyork.us/5bgh-vtsn
- The direct call to API is `https://data.cityofnewyork.us/resource/5bgh-vtsn.json`

#### Input

You have to expose an API endpoint accepting the parameters: 
- Coordinates of the fire (any format you prefer)
- Number of fire trucks N

```input json example
{
    "theGeom":{
        "type":"point",
        "coordinates":[40.43,-74.1]
    },
    "numberOfFireTrucks":3
}   
```

#### Output

The API endpoint should return a JSON object containing:
- total firehoses length in meters
- list of N nearest hydrants used by the fire brigade, with its unitId and distance to the fire
   
#### Example outcome from the API
 
```output json example
{
    "totalFirehosesLength": 1438,
    "hydrants": [
        {
          "unitid": "H325449",
              "distanceToFire": 24
        },        
        {
          "unitid": "H301843",
          "distanceToFire": 414
        },        
        {
          "unitid": "H325111",
          "distanceToFire": 1000
        }
    ] 
}
```
