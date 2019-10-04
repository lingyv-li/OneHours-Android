// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'database.dart';

// **************************************************************************
// MoorGenerator
// **************************************************************************

// ignore_for_file: unnecessary_brace_in_string_interps, unnecessary_this
class Card extends DataClass implements Insertable<Card> {
  final String id;
  final int averageTime;
  final String content;
  final String meta;
  final CardType type;
  Card(
      {@required this.id,
      @required this.averageTime,
      @required this.content,
      @required this.meta,
      @required this.type});
  factory Card.fromData(Map<String, dynamic> data, GeneratedDatabase db,
      {String prefix}) {
    final effectivePrefix = prefix ?? '';
    final stringType = db.typeSystem.forDartType<String>();
    final intType = db.typeSystem.forDartType<int>();
    return Card(
      id: stringType.mapFromDatabaseResponse(data['${effectivePrefix}id']),
      averageTime: intType
          .mapFromDatabaseResponse(data['${effectivePrefix}average_time']),
      content:
          stringType.mapFromDatabaseResponse(data['${effectivePrefix}content']),
      meta: stringType.mapFromDatabaseResponse(data['${effectivePrefix}meta']),
      type: $CardsTable.$converter0.mapToDart(
          intType.mapFromDatabaseResponse(data['${effectivePrefix}type'])),
    );
  }
  factory Card.fromJson(Map<String, dynamic> json,
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return Card(
      id: serializer.fromJson<String>(json['id']),
      averageTime: serializer.fromJson<int>(json['averageTime']),
      content: serializer.fromJson<String>(json['content']),
      meta: serializer.fromJson<String>(json['meta']),
      type: serializer.fromJson<CardType>(json['type']),
    );
  }
  @override
  Map<String, dynamic> toJson(
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return {
      'id': serializer.toJson<String>(id),
      'averageTime': serializer.toJson<int>(averageTime),
      'content': serializer.toJson<String>(content),
      'meta': serializer.toJson<String>(meta),
      'type': serializer.toJson<CardType>(type),
    };
  }

  @override
  T createCompanion<T extends UpdateCompanion<Card>>(bool nullToAbsent) {
    return CardsCompanion(
      id: id == null && nullToAbsent ? const Value.absent() : Value(id),
      averageTime: averageTime == null && nullToAbsent
          ? const Value.absent()
          : Value(averageTime),
      content: content == null && nullToAbsent
          ? const Value.absent()
          : Value(content),
      meta: meta == null && nullToAbsent ? const Value.absent() : Value(meta),
      type: type == null && nullToAbsent ? const Value.absent() : Value(type),
    ) as T;
  }

  Card copyWith(
          {String id,
          int averageTime,
          String content,
          String meta,
          CardType type}) =>
      Card(
        id: id ?? this.id,
        averageTime: averageTime ?? this.averageTime,
        content: content ?? this.content,
        meta: meta ?? this.meta,
        type: type ?? this.type,
      );
  @override
  String toString() {
    return (StringBuffer('Card(')
          ..write('id: $id, ')
          ..write('averageTime: $averageTime, ')
          ..write('content: $content, ')
          ..write('meta: $meta, ')
          ..write('type: $type')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => $mrjf($mrjc(
      id.hashCode,
      $mrjc(averageTime.hashCode,
          $mrjc(content.hashCode, $mrjc(meta.hashCode, type.hashCode)))));
  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Card &&
          other.id == this.id &&
          other.averageTime == this.averageTime &&
          other.content == this.content &&
          other.meta == this.meta &&
          other.type == this.type);
}

class CardsCompanion extends UpdateCompanion<Card> {
  final Value<String> id;
  final Value<int> averageTime;
  final Value<String> content;
  final Value<String> meta;
  final Value<CardType> type;
  const CardsCompanion({
    this.id = const Value.absent(),
    this.averageTime = const Value.absent(),
    this.content = const Value.absent(),
    this.meta = const Value.absent(),
    this.type = const Value.absent(),
  });
  CardsCompanion.insert({
    @required String id,
    this.averageTime = const Value.absent(),
    @required String content,
    this.meta = const Value.absent(),
    @required CardType type,
  })  : id = Value(id),
        content = Value(content),
        type = Value(type);
  CardsCompanion copyWith(
      {Value<String> id,
      Value<int> averageTime,
      Value<String> content,
      Value<String> meta,
      Value<CardType> type}) {
    return CardsCompanion(
      id: id ?? this.id,
      averageTime: averageTime ?? this.averageTime,
      content: content ?? this.content,
      meta: meta ?? this.meta,
      type: type ?? this.type,
    );
  }
}

class $CardsTable extends Cards with TableInfo<$CardsTable, Card> {
  final GeneratedDatabase _db;
  final String _alias;
  $CardsTable(this._db, [this._alias]);
  final VerificationMeta _idMeta = const VerificationMeta('id');
  GeneratedTextColumn _id;
  @override
  GeneratedTextColumn get id => _id ??= _constructId();
  GeneratedTextColumn _constructId() {
    return GeneratedTextColumn(
      'id',
      $tableName,
      false,
    );
  }

  final VerificationMeta _averageTimeMeta =
      const VerificationMeta('averageTime');
  GeneratedIntColumn _averageTime;
  @override
  GeneratedIntColumn get averageTime =>
      _averageTime ??= _constructAverageTime();
  GeneratedIntColumn _constructAverageTime() {
    return GeneratedIntColumn('average_time', $tableName, false,
        defaultValue: const Constant(0));
  }

  final VerificationMeta _contentMeta = const VerificationMeta('content');
  GeneratedTextColumn _content;
  @override
  GeneratedTextColumn get content => _content ??= _constructContent();
  GeneratedTextColumn _constructContent() {
    return GeneratedTextColumn(
      'content',
      $tableName,
      false,
    );
  }

  final VerificationMeta _metaMeta = const VerificationMeta('meta');
  GeneratedTextColumn _meta;
  @override
  GeneratedTextColumn get meta => _meta ??= _constructMeta();
  GeneratedTextColumn _constructMeta() {
    return GeneratedTextColumn('meta', $tableName, false,
        defaultValue: const Constant<String, StringType>(""));
  }

  final VerificationMeta _typeMeta = const VerificationMeta('type');
  GeneratedIntColumn _type;
  @override
  GeneratedIntColumn get type => _type ??= _constructType();
  GeneratedIntColumn _constructType() {
    return GeneratedIntColumn(
      'type',
      $tableName,
      false,
    );
  }

  @override
  List<GeneratedColumn> get $columns => [id, averageTime, content, meta, type];
  @override
  $CardsTable get asDslTable => this;
  @override
  String get $tableName => _alias ?? 'cards';
  @override
  final String actualTableName = 'cards';
  @override
  VerificationContext validateIntegrity(CardsCompanion d,
      {bool isInserting = false}) {
    final context = VerificationContext();
    if (d.id.present) {
      context.handle(_idMeta, id.isAcceptableValue(d.id.value, _idMeta));
    } else if (id.isRequired && isInserting) {
      context.missing(_idMeta);
    }
    if (d.averageTime.present) {
      context.handle(_averageTimeMeta,
          averageTime.isAcceptableValue(d.averageTime.value, _averageTimeMeta));
    } else if (averageTime.isRequired && isInserting) {
      context.missing(_averageTimeMeta);
    }
    if (d.content.present) {
      context.handle(_contentMeta,
          content.isAcceptableValue(d.content.value, _contentMeta));
    } else if (content.isRequired && isInserting) {
      context.missing(_contentMeta);
    }
    if (d.meta.present) {
      context.handle(
          _metaMeta, meta.isAcceptableValue(d.meta.value, _metaMeta));
    } else if (meta.isRequired && isInserting) {
      context.missing(_metaMeta);
    }
    context.handle(_typeMeta, const VerificationResult.success());
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  Card map(Map<String, dynamic> data, {String tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : null;
    return Card.fromData(data, _db, prefix: effectivePrefix);
  }

  @override
  Map<String, Variable> entityToSql(CardsCompanion d) {
    final map = <String, Variable>{};
    if (d.id.present) {
      map['id'] = Variable<String, StringType>(d.id.value);
    }
    if (d.averageTime.present) {
      map['average_time'] = Variable<int, IntType>(d.averageTime.value);
    }
    if (d.content.present) {
      map['content'] = Variable<String, StringType>(d.content.value);
    }
    if (d.meta.present) {
      map['meta'] = Variable<String, StringType>(d.meta.value);
    }
    if (d.type.present) {
      final converter = $CardsTable.$converter0;
      map['type'] = Variable<int, IntType>(converter.mapToSql(d.type.value));
    }
    return map;
  }

  @override
  $CardsTable createAlias(String alias) {
    return $CardsTable(_db, alias);
  }

  static _CardTypeConverter $converter0 = _CardTypeConverter();
}

class Tag extends DataClass implements Insertable<Tag> {
  final String id;
  final String name;
  final String parent;
  Tag({@required this.id, @required this.name, @required this.parent});
  factory Tag.fromData(Map<String, dynamic> data, GeneratedDatabase db,
      {String prefix}) {
    final effectivePrefix = prefix ?? '';
    final stringType = db.typeSystem.forDartType<String>();
    return Tag(
      id: stringType.mapFromDatabaseResponse(data['${effectivePrefix}id']),
      name: stringType.mapFromDatabaseResponse(data['${effectivePrefix}name']),
      parent:
          stringType.mapFromDatabaseResponse(data['${effectivePrefix}parent']),
    );
  }
  factory Tag.fromJson(Map<String, dynamic> json,
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return Tag(
      id: serializer.fromJson<String>(json['id']),
      name: serializer.fromJson<String>(json['name']),
      parent: serializer.fromJson<String>(json['parent']),
    );
  }
  @override
  Map<String, dynamic> toJson(
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return {
      'id': serializer.toJson<String>(id),
      'name': serializer.toJson<String>(name),
      'parent': serializer.toJson<String>(parent),
    };
  }

  @override
  T createCompanion<T extends UpdateCompanion<Tag>>(bool nullToAbsent) {
    return TagsCompanion(
      id: id == null && nullToAbsent ? const Value.absent() : Value(id),
      name: name == null && nullToAbsent ? const Value.absent() : Value(name),
      parent:
          parent == null && nullToAbsent ? const Value.absent() : Value(parent),
    ) as T;
  }

  Tag copyWith({String id, String name, String parent}) => Tag(
        id: id ?? this.id,
        name: name ?? this.name,
        parent: parent ?? this.parent,
      );
  @override
  String toString() {
    return (StringBuffer('Tag(')
          ..write('id: $id, ')
          ..write('name: $name, ')
          ..write('parent: $parent')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode =>
      $mrjf($mrjc(id.hashCode, $mrjc(name.hashCode, parent.hashCode)));
  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is Tag &&
          other.id == this.id &&
          other.name == this.name &&
          other.parent == this.parent);
}

class TagsCompanion extends UpdateCompanion<Tag> {
  final Value<String> id;
  final Value<String> name;
  final Value<String> parent;
  const TagsCompanion({
    this.id = const Value.absent(),
    this.name = const Value.absent(),
    this.parent = const Value.absent(),
  });
  TagsCompanion.insert({
    @required String id,
    @required String name,
    @required String parent,
  })  : id = Value(id),
        name = Value(name),
        parent = Value(parent);
  TagsCompanion copyWith(
      {Value<String> id, Value<String> name, Value<String> parent}) {
    return TagsCompanion(
      id: id ?? this.id,
      name: name ?? this.name,
      parent: parent ?? this.parent,
    );
  }
}

class $TagsTable extends Tags with TableInfo<$TagsTable, Tag> {
  final GeneratedDatabase _db;
  final String _alias;
  $TagsTable(this._db, [this._alias]);
  final VerificationMeta _idMeta = const VerificationMeta('id');
  GeneratedTextColumn _id;
  @override
  GeneratedTextColumn get id => _id ??= _constructId();
  GeneratedTextColumn _constructId() {
    return GeneratedTextColumn(
      'id',
      $tableName,
      false,
    );
  }

  final VerificationMeta _nameMeta = const VerificationMeta('name');
  GeneratedTextColumn _name;
  @override
  GeneratedTextColumn get name => _name ??= _constructName();
  GeneratedTextColumn _constructName() {
    return GeneratedTextColumn(
      'name',
      $tableName,
      false,
    );
  }

  final VerificationMeta _parentMeta = const VerificationMeta('parent');
  GeneratedTextColumn _parent;
  @override
  GeneratedTextColumn get parent => _parent ??= _constructParent();
  GeneratedTextColumn _constructParent() {
    return GeneratedTextColumn(
      'parent',
      $tableName,
      false,
    );
  }

  @override
  List<GeneratedColumn> get $columns => [id, name, parent];
  @override
  $TagsTable get asDslTable => this;
  @override
  String get $tableName => _alias ?? 'tags';
  @override
  final String actualTableName = 'tags';
  @override
  VerificationContext validateIntegrity(TagsCompanion d,
      {bool isInserting = false}) {
    final context = VerificationContext();
    if (d.id.present) {
      context.handle(_idMeta, id.isAcceptableValue(d.id.value, _idMeta));
    } else if (id.isRequired && isInserting) {
      context.missing(_idMeta);
    }
    if (d.name.present) {
      context.handle(
          _nameMeta, name.isAcceptableValue(d.name.value, _nameMeta));
    } else if (name.isRequired && isInserting) {
      context.missing(_nameMeta);
    }
    if (d.parent.present) {
      context.handle(
          _parentMeta, parent.isAcceptableValue(d.parent.value, _parentMeta));
    } else if (parent.isRequired && isInserting) {
      context.missing(_parentMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  Tag map(Map<String, dynamic> data, {String tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : null;
    return Tag.fromData(data, _db, prefix: effectivePrefix);
  }

  @override
  Map<String, Variable> entityToSql(TagsCompanion d) {
    final map = <String, Variable>{};
    if (d.id.present) {
      map['id'] = Variable<String, StringType>(d.id.value);
    }
    if (d.name.present) {
      map['name'] = Variable<String, StringType>(d.name.value);
    }
    if (d.parent.present) {
      map['parent'] = Variable<String, StringType>(d.parent.value);
    }
    return map;
  }

  @override
  $TagsTable createAlias(String alias) {
    return $TagsTable(_db, alias);
  }
}

class TagEntry extends DataClass implements Insertable<TagEntry> {
  final String card;
  final String tag;
  TagEntry({@required this.card, @required this.tag});
  factory TagEntry.fromData(Map<String, dynamic> data, GeneratedDatabase db,
      {String prefix}) {
    final effectivePrefix = prefix ?? '';
    final stringType = db.typeSystem.forDartType<String>();
    return TagEntry(
      card: stringType.mapFromDatabaseResponse(data['${effectivePrefix}card']),
      tag: stringType.mapFromDatabaseResponse(data['${effectivePrefix}tag']),
    );
  }
  factory TagEntry.fromJson(Map<String, dynamic> json,
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return TagEntry(
      card: serializer.fromJson<String>(json['card']),
      tag: serializer.fromJson<String>(json['tag']),
    );
  }
  @override
  Map<String, dynamic> toJson(
      {ValueSerializer serializer = const ValueSerializer.defaults()}) {
    return {
      'card': serializer.toJson<String>(card),
      'tag': serializer.toJson<String>(tag),
    };
  }

  @override
  T createCompanion<T extends UpdateCompanion<TagEntry>>(bool nullToAbsent) {
    return TagEntriesCompanion(
      card: card == null && nullToAbsent ? const Value.absent() : Value(card),
      tag: tag == null && nullToAbsent ? const Value.absent() : Value(tag),
    ) as T;
  }

  TagEntry copyWith({String card, String tag}) => TagEntry(
        card: card ?? this.card,
        tag: tag ?? this.tag,
      );
  @override
  String toString() {
    return (StringBuffer('TagEntry(')
          ..write('card: $card, ')
          ..write('tag: $tag')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => $mrjf($mrjc(card.hashCode, tag.hashCode));
  @override
  bool operator ==(other) =>
      identical(this, other) ||
      (other is TagEntry && other.card == this.card && other.tag == this.tag);
}

class TagEntriesCompanion extends UpdateCompanion<TagEntry> {
  final Value<String> card;
  final Value<String> tag;
  const TagEntriesCompanion({
    this.card = const Value.absent(),
    this.tag = const Value.absent(),
  });
  TagEntriesCompanion.insert({
    @required String card,
    @required String tag,
  })  : card = Value(card),
        tag = Value(tag);
  TagEntriesCompanion copyWith({Value<String> card, Value<String> tag}) {
    return TagEntriesCompanion(
      card: card ?? this.card,
      tag: tag ?? this.tag,
    );
  }
}

class $TagEntriesTable extends TagEntries
    with TableInfo<$TagEntriesTable, TagEntry> {
  final GeneratedDatabase _db;
  final String _alias;
  $TagEntriesTable(this._db, [this._alias]);
  final VerificationMeta _cardMeta = const VerificationMeta('card');
  GeneratedTextColumn _card;
  @override
  GeneratedTextColumn get card => _card ??= _constructCard();
  GeneratedTextColumn _constructCard() {
    return GeneratedTextColumn(
      'card',
      $tableName,
      false,
    );
  }

  final VerificationMeta _tagMeta = const VerificationMeta('tag');
  GeneratedTextColumn _tag;
  @override
  GeneratedTextColumn get tag => _tag ??= _constructTag();
  GeneratedTextColumn _constructTag() {
    return GeneratedTextColumn(
      'tag',
      $tableName,
      false,
    );
  }

  @override
  List<GeneratedColumn> get $columns => [card, tag];
  @override
  $TagEntriesTable get asDslTable => this;
  @override
  String get $tableName => _alias ?? 'tag_entries';
  @override
  final String actualTableName = 'tag_entries';
  @override
  VerificationContext validateIntegrity(TagEntriesCompanion d,
      {bool isInserting = false}) {
    final context = VerificationContext();
    if (d.card.present) {
      context.handle(
          _cardMeta, card.isAcceptableValue(d.card.value, _cardMeta));
    } else if (card.isRequired && isInserting) {
      context.missing(_cardMeta);
    }
    if (d.tag.present) {
      context.handle(_tagMeta, tag.isAcceptableValue(d.tag.value, _tagMeta));
    } else if (tag.isRequired && isInserting) {
      context.missing(_tagMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => <GeneratedColumn>{};
  @override
  TagEntry map(Map<String, dynamic> data, {String tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : null;
    return TagEntry.fromData(data, _db, prefix: effectivePrefix);
  }

  @override
  Map<String, Variable> entityToSql(TagEntriesCompanion d) {
    final map = <String, Variable>{};
    if (d.card.present) {
      map['card'] = Variable<String, StringType>(d.card.value);
    }
    if (d.tag.present) {
      map['tag'] = Variable<String, StringType>(d.tag.value);
    }
    return map;
  }

  @override
  $TagEntriesTable createAlias(String alias) {
    return $TagEntriesTable(_db, alias);
  }
}

abstract class _$Database extends GeneratedDatabase {
  _$Database(QueryExecutor e) : super(const SqlTypeSystem.withDefaults(), e);
  $CardsTable _cards;
  $CardsTable get cards => _cards ??= $CardsTable(this);
  $TagsTable _tags;
  $TagsTable get tags => _tags ??= $TagsTable(this);
  $TagEntriesTable _tagEntries;
  $TagEntriesTable get tagEntries => _tagEntries ??= $TagEntriesTable(this);
  Selectable<int> countCardsQuery() {
    return customSelectQuery('SELECT count(*) FROM cards;',
        variables: [],
        readsFrom: {cards}).map((QueryRow row) => row.readInt('count(*)'));
  }

  Future<List<int>> countCards() {
    return countCardsQuery().get();
  }

  Stream<List<int>> watchCountCards() {
    return countCardsQuery().watch();
  }

  @override
  List<TableInfo> get allTables => [cards, tags, tagEntries];
}
