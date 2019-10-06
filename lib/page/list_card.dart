import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:one_hours/database/database.dart' as database;
import 'package:one_hours/database/database.dart';
import 'package:provider/provider.dart';

enum _CardOption { delete }

class ListCardTab extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var db = Provider.of<Database>(context);

    return StreamBuilder<List<database.CardWithTags>>(
      stream: db.getCards(),
      initialData: [],
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          return ListView.separated(
            itemCount: snapshot.data.length,
            itemBuilder: (item, i) => ListTile(
              title: Text(snapshot.data[i].card.content),
              subtitle:
                  Text(snapshot.data[i].tags.map((tag) => tag.name).join(", ")),
              trailing: PopupMenuButton<_CardOption>(
                icon: Icon(Icons.more_vert),
                itemBuilder: (context) => [
                  const PopupMenuItem(
                    value: _CardOption.delete,
                    child: Text("Delete"),
                  ),
                ],
                onSelected: (option) {
                  switch (option) {
                    case _CardOption.delete:
                      db.deleteCard(snapshot.data[i].card);
                      break;
                  }
                },
              ),
              onLongPress: null,
              onTap: () {
                Navigator.pushNamed(context, "/card",
                    arguments: snapshot.data[i]);
              },
            ).build(context),
            separatorBuilder: (_, __) => Divider(
              height: 0,
            ),
          );
        } else if (snapshot.hasError) {
          Future(() => Scaffold.of(context).showSnackBar(SnackBar(
                content: Text(snapshot.error.toString()),
              )));

          return Text(snapshot.error.toString());
        } else {
          return CircularProgressIndicator();
        }
      },
    );
  }
}
