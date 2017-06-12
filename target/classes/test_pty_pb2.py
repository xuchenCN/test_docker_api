# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: test_pty.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor.FileDescriptor(
  name='test_pty.proto',
  package='',
  syntax='proto3',
  serialized_pb=_b('\n\x0etest_pty.proto\"\x1a\n\nCmdRequest\x12\x0c\n\x04\x62ody\x18\x01 \x01(\x0c\"\x1b\n\x0b\x43mdResponse\x12\x0c\n\x04\x62ody\x18\x01 \x01(\x0c\x32\x34\n\nPTYService\x12&\n\x07\x63ommand\x12\x0b.CmdRequest\x1a\x0c.CmdResponse0\x01\x42\x30\n\x17\x63om.lenovo.husky.dockerB\x0fTestPTYProtocol\x88\x01\x01\xa0\x01\x01\x62\x06proto3')
)
_sym_db.RegisterFileDescriptor(DESCRIPTOR)




_CMDREQUEST = _descriptor.Descriptor(
  name='CmdRequest',
  full_name='CmdRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='body', full_name='CmdRequest.body', index=0,
      number=1, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=18,
  serialized_end=44,
)


_CMDRESPONSE = _descriptor.Descriptor(
  name='CmdResponse',
  full_name='CmdResponse',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='body', full_name='CmdResponse.body', index=0,
      number=1, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=46,
  serialized_end=73,
)

DESCRIPTOR.message_types_by_name['CmdRequest'] = _CMDREQUEST
DESCRIPTOR.message_types_by_name['CmdResponse'] = _CMDRESPONSE

CmdRequest = _reflection.GeneratedProtocolMessageType('CmdRequest', (_message.Message,), dict(
  DESCRIPTOR = _CMDREQUEST,
  __module__ = 'test_pty_pb2'
  # @@protoc_insertion_point(class_scope:CmdRequest)
  ))
_sym_db.RegisterMessage(CmdRequest)

CmdResponse = _reflection.GeneratedProtocolMessageType('CmdResponse', (_message.Message,), dict(
  DESCRIPTOR = _CMDRESPONSE,
  __module__ = 'test_pty_pb2'
  # @@protoc_insertion_point(class_scope:CmdResponse)
  ))
_sym_db.RegisterMessage(CmdResponse)


DESCRIPTOR.has_options = True
DESCRIPTOR._options = _descriptor._ParseOptions(descriptor_pb2.FileOptions(), _b('\n\027com.lenovo.husky.dockerB\017TestPTYProtocol\210\001\001\240\001\001'))
try:
  # THESE ELEMENTS WILL BE DEPRECATED.
  # Please use the generated *_pb2_grpc.py files instead.
  import grpc
  from grpc.beta import implementations as beta_implementations
  from grpc.beta import interfaces as beta_interfaces
  from grpc.framework.common import cardinality
  from grpc.framework.interfaces.face import utilities as face_utilities


  class PTYServiceStub(object):

    def __init__(self, channel):
      """Constructor.

      Args:
        channel: A grpc.Channel.
      """
      self.command = channel.unary_stream(
          '/PTYService/command',
          request_serializer=CmdRequest.SerializeToString,
          response_deserializer=CmdResponse.FromString,
          )


  class PTYServiceServicer(object):

    def command(self, request, context):
      context.set_code(grpc.StatusCode.UNIMPLEMENTED)
      context.set_details('Method not implemented!')
      raise NotImplementedError('Method not implemented!')


  def add_PTYServiceServicer_to_server(servicer, server):
    rpc_method_handlers = {
        'command': grpc.unary_stream_rpc_method_handler(
            servicer.command,
            request_deserializer=CmdRequest.FromString,
            response_serializer=CmdResponse.SerializeToString,
        ),
    }
    generic_handler = grpc.method_handlers_generic_handler(
        'PTYService', rpc_method_handlers)
    server.add_generic_rpc_handlers((generic_handler,))


  class BetaPTYServiceServicer(object):
    """The Beta API is deprecated for 0.15.0 and later.

    It is recommended to use the GA API (classes and functions in this
    file not marked beta) for all further purposes. This class was generated
    only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0."""
    def command(self, request, context):
      context.code(beta_interfaces.StatusCode.UNIMPLEMENTED)


  class BetaPTYServiceStub(object):
    """The Beta API is deprecated for 0.15.0 and later.

    It is recommended to use the GA API (classes and functions in this
    file not marked beta) for all further purposes. This class was generated
    only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0."""
    def command(self, request, timeout, metadata=None, with_call=False, protocol_options=None):
      raise NotImplementedError()


  def beta_create_PTYService_server(servicer, pool=None, pool_size=None, default_timeout=None, maximum_timeout=None):
    """The Beta API is deprecated for 0.15.0 and later.

    It is recommended to use the GA API (classes and functions in this
    file not marked beta) for all further purposes. This function was
    generated only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0"""
    request_deserializers = {
      ('PTYService', 'command'): CmdRequest.FromString,
    }
    response_serializers = {
      ('PTYService', 'command'): CmdResponse.SerializeToString,
    }
    method_implementations = {
      ('PTYService', 'command'): face_utilities.unary_stream_inline(servicer.command),
    }
    server_options = beta_implementations.server_options(request_deserializers=request_deserializers, response_serializers=response_serializers, thread_pool=pool, thread_pool_size=pool_size, default_timeout=default_timeout, maximum_timeout=maximum_timeout)
    return beta_implementations.server(method_implementations, options=server_options)


  def beta_create_PTYService_stub(channel, host=None, metadata_transformer=None, pool=None, pool_size=None):
    """The Beta API is deprecated for 0.15.0 and later.

    It is recommended to use the GA API (classes and functions in this
    file not marked beta) for all further purposes. This function was
    generated only to ease transition from grpcio<0.15.0 to grpcio>=0.15.0"""
    request_serializers = {
      ('PTYService', 'command'): CmdRequest.SerializeToString,
    }
    response_deserializers = {
      ('PTYService', 'command'): CmdResponse.FromString,
    }
    cardinalities = {
      'command': cardinality.Cardinality.UNARY_STREAM,
    }
    stub_options = beta_implementations.stub_options(host=host, metadata_transformer=metadata_transformer, request_serializers=request_serializers, response_deserializers=response_deserializers, thread_pool=pool, thread_pool_size=pool_size)
    return beta_implementations.dynamic_stub(channel, 'PTYService', cardinalities, options=stub_options)
except ImportError:
  pass
# @@protoc_insertion_point(module_scope)
