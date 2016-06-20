
#ifndef TEST_SERIAL_H
#define TEST_SERIAL_H

typedef nx_struct test_serial_msg {
  nx_uint16_t nodeid;
  nx_uint8_t power;
  nx_int8_t rssi;
  nx_uint8_t lqi;
  nx_uint32_t localtime;  /**定義了本地時間**/
  nx_uint32_t offset;
} test_serial_msg_t;

enum {
  AM_TEST_SERIAL_MSG = 0x6,
};

#endif
