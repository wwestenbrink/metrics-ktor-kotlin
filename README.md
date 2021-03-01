# Intro

Implementation of a metrics service for receiving the running speed of different production lines and providing min, max
and arithmetic mean metrics. All data is stored in memory, and the API is thread safe.

# Notes on implementation

Stores the measurements for each production line in a ConcurrentSkipListMap, ordered by timestamp in millisecond
resolution. Measurements with the same timestamp will be overwritten. Outdated measurements are removed after every
submit. Metrics are re-calculated on every request.

# Starting the server

`./gradlew run`

# Submitting line speeds

Use your favourite rest client to submit some json data to `http://localhost:8080/linespeed`

- `line_id`: a long, specifying the id of the line, lines `1`,`2` and `3` are available.
- `speed`: a floating-point number, specifying the measured speed of the line.
- `timestamp`: A long, specifying the time, when the speed was measured, in epoch in millis in UTC time zone (this is
  not the current timestamp)

# Retrieving metrics

Use your favourite rest client to retrieve your metrics from `http://localhost:8080/metrics/{line_id}`

- `avg`: is a floating-point number specifying the arithmetic mean of the speed values of the last 60 minutes
- `max`: is a floating-point number specifying the highest speed value in the last 60 minutes
- `min`: is a floating-point number specifying the lowest speed value the last 60 minutes