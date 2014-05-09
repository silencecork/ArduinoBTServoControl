#include <SoftwareSerial.h>
#include <Servo.h>

const int RX = 8;
const int TX = 6;
const int SERVO = 11;
Servo servo;

SoftwareSerial blueToothSerial(RX, TX);

void setup() {
  Serial.begin(9600);
  while (!Serial) {
    ;
  }
  blueToothSerial.begin(38400);

  servo.attach(SERVO);

  Serial.println("Setup Finish");
}

void loop() {
  if(blueToothSerial.available()){
    char c;
    String str; 
    while ((c = blueToothSerial.read()) > 0) {
      str += c;
    }
    int degree = str.toInt();
    Serial.println(degree);
    if (degree < 0 || degree > 179) {
      return;
    }
    servo.write(degree);
  }
  delay(15);
}

