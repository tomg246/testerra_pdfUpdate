// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: framework.proto

package eu.tsystems.mms.tic.testframework.report.model;

public interface ClassContextOrBuilder extends
    // @@protoc_insertion_point(interface_extends:data.ClassContext)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.data.ContextValues context_values = 1;</code>
   * @return Whether the contextValues field is set.
   */
  boolean hasContextValues();
  /**
   * <code>.data.ContextValues context_values = 1;</code>
   * @return The contextValues.
   */
  eu.tsystems.mms.tic.testframework.report.model.ContextValues getContextValues();
  /**
   * <code>.data.ContextValues context_values = 1;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ContextValuesOrBuilder getContextValuesOrBuilder();

  /**
   * <code>repeated string method_context_ids = 6;</code>
   * @return A list containing the methodContextIds.
   */
  java.util.List<java.lang.String>
      getMethodContextIdsList();
  /**
   * <code>repeated string method_context_ids = 6;</code>
   * @return The count of methodContextIds.
   */
  int getMethodContextIdsCount();
  /**
   * <code>repeated string method_context_ids = 6;</code>
   * @param index The index of the element to return.
   * @return The methodContextIds at the given index.
   */
  java.lang.String getMethodContextIds(int index);
  /**
   * <code>repeated string method_context_ids = 6;</code>
   * @param index The index of the value to return.
   * @return The bytes of the methodContextIds at the given index.
   */
  com.google.protobuf.ByteString
      getMethodContextIdsBytes(int index);

  /**
   * <code>string full_class_name = 7;</code>
   * @return The fullClassName.
   */
  java.lang.String getFullClassName();
  /**
   * <code>string full_class_name = 7;</code>
   * @return The bytes for fullClassName.
   */
  com.google.protobuf.ByteString
      getFullClassNameBytes();

  /**
   * <code>string simple_class_name = 8;</code>
   * @return The simpleClassName.
   */
  java.lang.String getSimpleClassName();
  /**
   * <code>string simple_class_name = 8;</code>
   * @return The bytes for simpleClassName.
   */
  com.google.protobuf.ByteString
      getSimpleClassNameBytes();

  /**
   * <code>string test_context_id = 9;</code>
   * @return The testContextId.
   */
  java.lang.String getTestContextId();
  /**
   * <code>string test_context_id = 9;</code>
   * @return The bytes for testContextId.
   */
  com.google.protobuf.ByteString
      getTestContextIdBytes();

  /**
   * <code>string execution_context_id = 10;</code>
   * @return The executionContextId.
   */
  java.lang.String getExecutionContextId();
  /**
   * <code>string execution_context_id = 10;</code>
   * @return The bytes for executionContextId.
   */
  com.google.protobuf.ByteString
      getExecutionContextIdBytes();

  /**
   * <code>string test_context_name = 11;</code>
   * @return The testContextName.
   */
  java.lang.String getTestContextName();
  /**
   * <code>string test_context_name = 11;</code>
   * @return The bytes for testContextName.
   */
  com.google.protobuf.ByteString
      getTestContextNameBytes();

  /**
   * <code>bool merged = 12 [deprecated = true];</code>
   * @return The merged.
   */
  @java.lang.Deprecated boolean getMerged();
}
