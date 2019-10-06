import 'dart:developer';
import 'dart:io';
import 'dart:ui';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:one_hours/database/database.dart';
import 'package:tuple/tuple.dart';

class DrawingPainter extends CustomPainter {
  const DrawingPainter(this.pointsList);

  final List<Tuple2<List<Offset>, Paint>> pointsList;

  @override
  void paint(Canvas canvas, Size size) => pointsList.forEach(
      (line) => canvas.drawPoints(PointMode.polygon, line.item1, line.item2));

  @override
  bool shouldRepaint(DrawingPainter oldDelegate) =>
      listEquals(pointsList, oldDelegate.pointsList);
}

class CanvasWidget extends StatefulWidget {
  final Paint paint;

  const CanvasWidget({Key key, this.paint}) : super(key: key);

  @override
  _CanvasWidgetState createState() => _CanvasWidgetState();
}

class _CanvasWidgetState extends State<CanvasWidget> {
  File _image;

  @override
  void initState() {
    getImage();
  }

  Future getImage() async {
    log("Hey!");
    var image = await ImagePicker.pickImage(source: ImageSource.camera);

    setState(() {
      _image = image;
    });
  }

  final pointsList = <Tuple2<List<Offset>, Paint>>[];

  @override
  Widget build(BuildContext context) {
    return _image != null
        ? GestureDetector(
            onPanStart: (details) {
              setState(() => pointsList
                  .add(Tuple2([details.localPosition], widget.paint)));
            },
            onPanUpdate: (details) {
              setState(() => pointsList.last.item1.add(details.localPosition));
            },
            child: CustomPaint(
                child: Image.file(_image), painter: DrawingPainter(pointsList)),
          )
        : Text("Image not selected");
  }
}
