import 'package:flutter/material.dart';
import 'package:one_hours/database/database.dart';
import 'package:one_hours/database/database.dart' as database;
import 'package:provider/provider.dart';

class EditCardPage extends StatefulWidget {
  final database.CardWithTags card;

  const EditCardPage({Key key, this.card}) : super(key: key);

  @override
  _EditCardPageState createState() => _EditCardPageState();
}

class _EditCardPageState extends State<EditCardPage> {
  var _contentController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    _contentController.text = widget.card?.card?.content;
    final db = Provider.of<Database>(context);
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.card == null ? "Add card" : "Edit card"),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.check),
            onPressed: () {
              db.insertCard(_contentController.text, CardType.text);
              Navigator.pop(context);
            },
          )
        ],
      ),
      body: Container(
        child: SizedBox.expand(
          child: TextField(
            controller: _contentController,
            keyboardType: TextInputType.multiline,
            maxLines: null,
          ),
        ),
      ),
      bottomNavigationBar: BottomAppBar(
          child: Row(
        children: <Widget>[
          FlatButton(
            onPressed: () {},
            child: Text("HI"),
          ),
          FlatButton(
            onPressed: () {},
            child: Text("THERE"),
          ),
        ],
      )),
    );
  }
}
