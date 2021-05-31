import boto3
import json
import sys
import pika
import pathlib
import os

from PIL import Image

import pytesseract


class ImageRecognition:
    def __init__(self):
        self.credentials_path = pathlib.Path('aws_credentials.json')
        with open(self.credentials_path) as f:
            self.credentials = json.load(f)
        self.client = boto3.client('rekognition',
                                   region_name='us-east-1',
                                   aws_access_key_id=self.credentials['aws_access_key_id'],
                                   aws_secret_access_key=self.credentials['aws_secret_access_key'],
                                   aws_session_token=self.credentials['aws_session_token']
                                   )

        self.response = []
        self.pathIn = ""
        self.content = []

        if len(sys.argv) > 1:
            rabbit_host = sys.argv[1]
        else:
            rabbit_host = "localhost"

        # RabbitMQ initialization
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(host=rabbit_host, port=5672))
        self.image_channel = self.connection.channel()
        self.image_channel.basic_qos(prefetch_count=1)


        self.image_channel.queue_declare(queue="jpg")
        self.image_channel.basic_consume(queue="jpg",on_message_callback=self.callback)

        self.image_channel.queue_declare(queue="png")
        self.image_channel.basic_consume(queue="png",on_message_callback=self.callback)

    def detect_image(self):
        with open(self.pathIn, 'rb') as image:
            try:
                self.response = self.client.detect_labels(Image={'Bytes': image.read()})
            except:
                self.response=pytesseract.image_to_string(Image.open(self.pathIn))


    def callback(self, ch, method, properties, body):
        # run aws recognition

        tmp_path = str(body.decode())
        self.pathIn = pathlib.Path('../../storage',tmp_path)
        self.detect_image()

        try:
            for label in self.response['Labels']:
                self.content.append(str(label['Name']))
        except:
            self.content=['']
            
        ch.basic_publish(
                    exchange='',
                    routing_key=properties.reply_to,
                    properties=pika.BasicProperties(),
                    body=str(self.content))
        ch.basic_ack(delivery_tag=method.delivery_tag)

        self.response = []
        self.content = []

if __name__ == "__main__":
    recognizer = ImageRecognition()
    recognizer.image_channel.start_consuming()

