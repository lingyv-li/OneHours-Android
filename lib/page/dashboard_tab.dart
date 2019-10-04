import 'package:flutter/material.dart';
import 'package:tuple/tuple.dart';

class DashboardTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: <Widget>[
        Text("Hello"),
        IconTheme(
          data: IconThemeData(
            color: Colors.white,
            size: 46,
          ),
          child: Row(
              children: <Tuple4<Function, Color, IconData, String>>[
            Tuple4(() {
              Navigator.of(context).pushNamed("/card");
            }, Colors.blue, Icons.add_circle, "Add"),
            Tuple4(() {}, Colors.amber, Icons.timer, "Start"),
            Tuple4(() {}, Colors.green, Icons.library_books, "Goal"),
          ]
                  .map(
                    (e) => Expanded(
                      child: Container(
                        color: e.item2,
                        child: SafeArea(
                          minimum: EdgeInsets.symmetric(vertical: 10),
                          child: InkWell(
                            onTap: e.item1,
                            child: Column(
                              children: <Widget>[
                                Icon(e.item3),
                                Text(
                                  e.item4,
                                  style: TextStyle(color: Colors.white),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ),
                  )
                  .toList()),
        )
      ],
    );
  }
}
