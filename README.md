# Reward app

## 1. Run application for local development

### Start back-end

```bash
gradle bootRun
```
- By default, back-end use port 8088
- Java 17

#### End-points

One POST end-point is implemented:
`"http://localhost:8088/api/calculate-reward"`

Sample request body:
```bash
[
    {
       "cost": "120",
       "time": "1667852953"
    }
]
```
