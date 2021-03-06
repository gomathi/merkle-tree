/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *//**
 * Autogenerated by Thrift Compiler (0.8.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.hashtrees.thrift.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rebuild hashtree request object.
 * 
 * @param requester, server name which requests for the rebuild
 * @param treeId
 * @param tokenNo, a unique tokenNo to differentiate similar requests.
 * @param expFullRebuildTimeInt, if the remote tree is not fully rebuilt
 *        within this interval, then remote tree is expected to do a full
 *        rebuild, otherwise just dirty segments rebuild.
 */
public class RebuildHashTreeRequest implements org.apache.thrift.TBase<RebuildHashTreeRequest, RebuildHashTreeRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RebuildHashTreeRequest");

  private static final org.apache.thrift.protocol.TField REQUESTER_FIELD_DESC = new org.apache.thrift.protocol.TField("requester", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("treeId", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField TOKEN_NO_FIELD_DESC = new org.apache.thrift.protocol.TField("tokenNo", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField EXP_FULL_REBUILD_TIME_INT_FIELD_DESC = new org.apache.thrift.protocol.TField("expFullRebuildTimeInt", org.apache.thrift.protocol.TType.I64, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RebuildHashTreeRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RebuildHashTreeRequestTupleSchemeFactory());
  }

  public ServerName requester; // required
  public long treeId; // required
  public long tokenNo; // required
  public long expFullRebuildTimeInt; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUESTER((short)1, "requester"),
    TREE_ID((short)2, "treeId"),
    TOKEN_NO((short)3, "tokenNo"),
    EXP_FULL_REBUILD_TIME_INT((short)4, "expFullRebuildTimeInt");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // REQUESTER
          return REQUESTER;
        case 2: // TREE_ID
          return TREE_ID;
        case 3: // TOKEN_NO
          return TOKEN_NO;
        case 4: // EXP_FULL_REBUILD_TIME_INT
          return EXP_FULL_REBUILD_TIME_INT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TREEID_ISSET_ID = 0;
  private static final int __TOKENNO_ISSET_ID = 1;
  private static final int __EXPFULLREBUILDTIMEINT_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUESTER, new org.apache.thrift.meta_data.FieldMetaData("requester", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ServerName.class)));
    tmpMap.put(_Fields.TREE_ID, new org.apache.thrift.meta_data.FieldMetaData("treeId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.TOKEN_NO, new org.apache.thrift.meta_data.FieldMetaData("tokenNo", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.EXP_FULL_REBUILD_TIME_INT, new org.apache.thrift.meta_data.FieldMetaData("expFullRebuildTimeInt", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RebuildHashTreeRequest.class, metaDataMap);
  }

  public RebuildHashTreeRequest() {
  }

  public RebuildHashTreeRequest(
    ServerName requester,
    long treeId,
    long tokenNo,
    long expFullRebuildTimeInt)
  {
    this();
    this.requester = requester;
    this.treeId = treeId;
    setTreeIdIsSet(true);
    this.tokenNo = tokenNo;
    setTokenNoIsSet(true);
    this.expFullRebuildTimeInt = expFullRebuildTimeInt;
    setExpFullRebuildTimeIntIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RebuildHashTreeRequest(RebuildHashTreeRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetRequester()) {
      this.requester = new ServerName(other.requester);
    }
    this.treeId = other.treeId;
    this.tokenNo = other.tokenNo;
    this.expFullRebuildTimeInt = other.expFullRebuildTimeInt;
  }

  public RebuildHashTreeRequest deepCopy() {
    return new RebuildHashTreeRequest(this);
  }

  @Override
  public void clear() {
    this.requester = null;
    setTreeIdIsSet(false);
    this.treeId = 0;
    setTokenNoIsSet(false);
    this.tokenNo = 0;
    setExpFullRebuildTimeIntIsSet(false);
    this.expFullRebuildTimeInt = 0;
  }

  public ServerName getRequester() {
    return this.requester;
  }

  public RebuildHashTreeRequest setRequester(ServerName requester) {
    this.requester = requester;
    return this;
  }

  public void unsetRequester() {
    this.requester = null;
  }

  /** Returns true if field requester is set (has been assigned a value) and false otherwise */
  public boolean isSetRequester() {
    return this.requester != null;
  }

  public void setRequesterIsSet(boolean value) {
    if (!value) {
      this.requester = null;
    }
  }

  public long getTreeId() {
    return this.treeId;
  }

  public RebuildHashTreeRequest setTreeId(long treeId) {
    this.treeId = treeId;
    setTreeIdIsSet(true);
    return this;
  }

  public void unsetTreeId() {
    __isset_bit_vector.clear(__TREEID_ISSET_ID);
  }

  /** Returns true if field treeId is set (has been assigned a value) and false otherwise */
  public boolean isSetTreeId() {
    return __isset_bit_vector.get(__TREEID_ISSET_ID);
  }

  public void setTreeIdIsSet(boolean value) {
    __isset_bit_vector.set(__TREEID_ISSET_ID, value);
  }

  public long getTokenNo() {
    return this.tokenNo;
  }

  public RebuildHashTreeRequest setTokenNo(long tokenNo) {
    this.tokenNo = tokenNo;
    setTokenNoIsSet(true);
    return this;
  }

  public void unsetTokenNo() {
    __isset_bit_vector.clear(__TOKENNO_ISSET_ID);
  }

  /** Returns true if field tokenNo is set (has been assigned a value) and false otherwise */
  public boolean isSetTokenNo() {
    return __isset_bit_vector.get(__TOKENNO_ISSET_ID);
  }

  public void setTokenNoIsSet(boolean value) {
    __isset_bit_vector.set(__TOKENNO_ISSET_ID, value);
  }

  public long getExpFullRebuildTimeInt() {
    return this.expFullRebuildTimeInt;
  }

  public RebuildHashTreeRequest setExpFullRebuildTimeInt(long expFullRebuildTimeInt) {
    this.expFullRebuildTimeInt = expFullRebuildTimeInt;
    setExpFullRebuildTimeIntIsSet(true);
    return this;
  }

  public void unsetExpFullRebuildTimeInt() {
    __isset_bit_vector.clear(__EXPFULLREBUILDTIMEINT_ISSET_ID);
  }

  /** Returns true if field expFullRebuildTimeInt is set (has been assigned a value) and false otherwise */
  public boolean isSetExpFullRebuildTimeInt() {
    return __isset_bit_vector.get(__EXPFULLREBUILDTIMEINT_ISSET_ID);
  }

  public void setExpFullRebuildTimeIntIsSet(boolean value) {
    __isset_bit_vector.set(__EXPFULLREBUILDTIMEINT_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case REQUESTER:
      if (value == null) {
        unsetRequester();
      } else {
        setRequester((ServerName)value);
      }
      break;

    case TREE_ID:
      if (value == null) {
        unsetTreeId();
      } else {
        setTreeId((Long)value);
      }
      break;

    case TOKEN_NO:
      if (value == null) {
        unsetTokenNo();
      } else {
        setTokenNo((Long)value);
      }
      break;

    case EXP_FULL_REBUILD_TIME_INT:
      if (value == null) {
        unsetExpFullRebuildTimeInt();
      } else {
        setExpFullRebuildTimeInt((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case REQUESTER:
      return getRequester();

    case TREE_ID:
      return Long.valueOf(getTreeId());

    case TOKEN_NO:
      return Long.valueOf(getTokenNo());

    case EXP_FULL_REBUILD_TIME_INT:
      return Long.valueOf(getExpFullRebuildTimeInt());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case REQUESTER:
      return isSetRequester();
    case TREE_ID:
      return isSetTreeId();
    case TOKEN_NO:
      return isSetTokenNo();
    case EXP_FULL_REBUILD_TIME_INT:
      return isSetExpFullRebuildTimeInt();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RebuildHashTreeRequest)
      return this.equals((RebuildHashTreeRequest)that);
    return false;
  }

  public boolean equals(RebuildHashTreeRequest that) {
    if (that == null)
      return false;

    boolean this_present_requester = true && this.isSetRequester();
    boolean that_present_requester = true && that.isSetRequester();
    if (this_present_requester || that_present_requester) {
      if (!(this_present_requester && that_present_requester))
        return false;
      if (!this.requester.equals(that.requester))
        return false;
    }

    boolean this_present_treeId = true;
    boolean that_present_treeId = true;
    if (this_present_treeId || that_present_treeId) {
      if (!(this_present_treeId && that_present_treeId))
        return false;
      if (this.treeId != that.treeId)
        return false;
    }

    boolean this_present_tokenNo = true;
    boolean that_present_tokenNo = true;
    if (this_present_tokenNo || that_present_tokenNo) {
      if (!(this_present_tokenNo && that_present_tokenNo))
        return false;
      if (this.tokenNo != that.tokenNo)
        return false;
    }

    boolean this_present_expFullRebuildTimeInt = true;
    boolean that_present_expFullRebuildTimeInt = true;
    if (this_present_expFullRebuildTimeInt || that_present_expFullRebuildTimeInt) {
      if (!(this_present_expFullRebuildTimeInt && that_present_expFullRebuildTimeInt))
        return false;
      if (this.expFullRebuildTimeInt != that.expFullRebuildTimeInt)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(RebuildHashTreeRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    RebuildHashTreeRequest typedOther = (RebuildHashTreeRequest)other;

    lastComparison = Boolean.valueOf(isSetRequester()).compareTo(typedOther.isSetRequester());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRequester()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.requester, typedOther.requester);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(typedOther.isSetTreeId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTreeId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.treeId, typedOther.treeId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTokenNo()).compareTo(typedOther.isSetTokenNo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTokenNo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tokenNo, typedOther.tokenNo);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetExpFullRebuildTimeInt()).compareTo(typedOther.isSetExpFullRebuildTimeInt());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetExpFullRebuildTimeInt()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.expFullRebuildTimeInt, typedOther.expFullRebuildTimeInt);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RebuildHashTreeRequest(");
    boolean first = true;

    sb.append("requester:");
    if (this.requester == null) {
      sb.append("null");
    } else {
      sb.append(this.requester);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("treeId:");
    sb.append(this.treeId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("tokenNo:");
    sb.append(this.tokenNo);
    first = false;
    if (!first) sb.append(", ");
    sb.append("expFullRebuildTimeInt:");
    sb.append(this.expFullRebuildTimeInt);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (requester == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'requester' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'treeId' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'tokenNo' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'expFullRebuildTimeInt' because it's a primitive and you chose the non-beans generator.
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RebuildHashTreeRequestStandardSchemeFactory implements SchemeFactory {
    public RebuildHashTreeRequestStandardScheme getScheme() {
      return new RebuildHashTreeRequestStandardScheme();
    }
  }

  private static class RebuildHashTreeRequestStandardScheme extends StandardScheme<RebuildHashTreeRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RebuildHashTreeRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // REQUESTER
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.requester = new ServerName();
              struct.requester.read(iprot);
              struct.setRequesterIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TREE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.treeId = iprot.readI64();
              struct.setTreeIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TOKEN_NO
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.tokenNo = iprot.readI64();
              struct.setTokenNoIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // EXP_FULL_REBUILD_TIME_INT
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.expFullRebuildTimeInt = iprot.readI64();
              struct.setExpFullRebuildTimeIntIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetTreeId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'treeId' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetTokenNo()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'tokenNo' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetExpFullRebuildTimeInt()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'expFullRebuildTimeInt' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, RebuildHashTreeRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.requester != null) {
        oprot.writeFieldBegin(REQUESTER_FIELD_DESC);
        struct.requester.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
      oprot.writeI64(struct.treeId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(TOKEN_NO_FIELD_DESC);
      oprot.writeI64(struct.tokenNo);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(EXP_FULL_REBUILD_TIME_INT_FIELD_DESC);
      oprot.writeI64(struct.expFullRebuildTimeInt);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RebuildHashTreeRequestTupleSchemeFactory implements SchemeFactory {
    public RebuildHashTreeRequestTupleScheme getScheme() {
      return new RebuildHashTreeRequestTupleScheme();
    }
  }

  private static class RebuildHashTreeRequestTupleScheme extends TupleScheme<RebuildHashTreeRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RebuildHashTreeRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct.requester.write(oprot);
      oprot.writeI64(struct.treeId);
      oprot.writeI64(struct.tokenNo);
      oprot.writeI64(struct.expFullRebuildTimeInt);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RebuildHashTreeRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.requester = new ServerName();
      struct.requester.read(iprot);
      struct.setRequesterIsSet(true);
      struct.treeId = iprot.readI64();
      struct.setTreeIdIsSet(true);
      struct.tokenNo = iprot.readI64();
      struct.setTokenNoIsSet(true);
      struct.expFullRebuildTimeInt = iprot.readI64();
      struct.setExpFullRebuildTimeIntIsSet(true);
    }
  }

}

