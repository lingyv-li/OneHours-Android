import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:one_hours/database/database.dart';
import 'package:one_hours/database/database.dart' as database;
import 'package:one_hours/page/edit_card.dart';
import 'package:one_hours/page/list_card.dart';
import 'package:provider/provider.dart';

import 'page/dashboard_tab.dart';

void main() => runApp(Provider<Database>(
    builder: (context) => Database(),
    dispose: (context, db) => db.close(),
    child: MyApp()));

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      onGenerateRoute: (settings) {
        var widget;
        switch (settings.name) {
          case "/":
            widget = MyHomePage(title: 'One Hours');
            break;
          case "/card":
            if (settings.arguments is database.CardWithTags) {
              widget = EditCardPage(card: settings.arguments);
            } else {
              widget = EditCardPage();
            }
            break;
        }
        return MaterialPageRoute(builder: (_) => widget);
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          bottom: TabBar(
            labelPadding: EdgeInsets.symmetric(vertical: 10),
            tabs: <Widget>[Text("MAIN"), Text("LIST")],
          ),
          title: Text(widget.title),
        ),
        body: TabBarView(
          children: <Widget>[
            DashboardTab(),
            ListCardTab(),
          ],
        ),
      ),
    );
  }
}
