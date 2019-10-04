import 'dart:async';

import 'package:moor_flutter/moor_flutter.dart';
import 'package:uuid/uuid.dart';

part 'database.g.dart';

enum CardType { text, image }

class _CardTypeConverter extends TypeConverter<CardType, int> {
  @override
  CardType mapToDart(int fromDb) {
    switch (fromDb) {
      case 1:
        return CardType.text;
      case 2:
        return CardType.image;
    }
    return null;
  }

  @override
  int mapToSql(CardType value) {
    switch (value) {
      case CardType.text:
        return 1;
      case CardType.image:
        return 2;
    }
    return 0;
  }
}

class Cards extends Table {
  TextColumn get id => text()();

  IntColumn get averageTime => integer().withDefault(const Constant(0))();

  TextColumn get content => text()();

  TextColumn get meta =>
      text().withDefault(const Constant<String, StringType>(""))();

  IntColumn get type => integer().map(_CardTypeConverter())();

  @override
  Set<Column> get primaryKey => {id};
}

class Tags extends Table {
  TextColumn get id => text()();

  TextColumn get name => text()();

  TextColumn get parent => text()();

  @override
  Set<Column> get primaryKey => {id};
}

@DataClassName('TagEntry')
class TagEntries extends Table {
  TextColumn get card => text()();

  TextColumn get tag => text()();
}

class CardWithTags {
  final Card card;
  final List<Tag> tags;

  CardWithTags(this.card, this.tags);
}

@UseMoor(
  tables: [Cards, Tags, TagEntries],
  queries: {
    "countCards": """
    SELECT count(*) FROM cards;
    """
  },
)
class Database extends _$Database {
  Database()
      : super(FlutterQueryExecutor.inDatabaseFolder(
            path: 'db.sqlite', logStatements: true));

  @override
  int get schemaVersion => 1;

  @override
  MigrationStrategy get migration {
    return MigrationStrategy(
      onCreate: (Migrator m) {
        return m.createAllTables();
      },
      onUpgrade: (Migrator m, int from, int to) async {
        return m.createAllTables();
      },
      beforeOpen: (details) async {
        if (details.wasCreated) {}
      },
    );
  }

  Future<CardWithTags> loadTags(Card card) async {
    final tag = await (select(tagEntries)
          ..where((_) => tagEntries.card.equals(card.id)))
        .join([innerJoin(tags, tags.id.equalsExp(tagEntries.tag))]).get();
    return CardWithTags(card, tag.map((row) => row.readTable(tags)).toList());
  }

  Future<int> insertCard(content, type, {meta: const Value<String>.absent()}) {
    return into(cards).insert(CardsCompanion.insert(
        id: Uuid().v4(), content: content, type: type, meta: meta));
  }

  Stream<List<CardWithTags>> getCards() {
    final cardsStream = select(cards).watch();
    return cardsStream.asyncMap(
        (rows) async => (await Future.wait(rows.map(loadTags))).toList());
  }

  Future<int> deleteCard(Card card) {
    return delete(cards).delete(card);
  }
}
