import 'package:flutter/material.dart';

class TextEditWidget extends StatelessWidget {
  final contentController;

  const TextEditWidget(this.contentController, {Key key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: SizedBox.expand(
        child: TextField(
          controller: contentController,
          keyboardType: TextInputType.multiline,
          maxLines: null,
        ),
      ),
    );
  }
}
