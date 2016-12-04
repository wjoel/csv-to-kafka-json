# csv-to-kafka-json

A simple program to send a CSV file to Kafka as JSON encoded messages.
The CSV file's header specifies the names of the columns, and those will
be used as keys in the JSON messages. Each row is sent as a separate
message.

## Usage

The program currently assumes that the first row in the CSV file will
contain the names of the columns.

    $ java -jar csv-to-kafka.jar [options]

## Options

`--bootstrap-servers`: A list of host/port pairs to connect to Kafka.
                       See [the Kafka documentation](https://kafka.apache.org/documentation#producerconfigs)
                       for more information.
`--topic`: Name of the Kafka topic to send the messages to.
`--filename`: Path to the CSV file to be sent.

## Examples

    $ java -jar csv-to-kafka.jar
        --bootstrap-servers localhost:9092
        --topic hello-csv
        --filename /path/to/some/csv-file-with-header.csv

## License

Copyright © 2016 Joel Wilsson

Distributed under the MIT license.
