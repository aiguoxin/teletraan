import os
import subprocess
import tempfile
import unittest
import mock
import tests

from deployd.common.executor import Executor
from deployd.common.types import AgentStatus


class TestUtilsFunctions(tests.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.fdout, cls.fdout_fn = tempfile.mkstemp()
        cls.pingServer = mock.Mock()
        cls.pingServer.__call__ = mock.Mock(return_value=False)
        cls.executor = Executor(callback=cls.pingServer)
        cls.executor.LOG_FILENAME = cls.fdout_fn

    @classmethod
    def tearDownClass(cls):
        os.close(cls.fdout)
        os.remove(cls.fdout_fn)

    def test_run_bad_script(self):
        fdout_fn = tempfile.mkstemp()[1]
        with open(fdout_fn, 'w') as f:
            f.write('echo hello')
        os.chmod(fdout_fn, 0755)

        ping_server = mock.Mock(return_value=True)
        executor = Executor(callback=ping_server)
        executor.LOG_FILENAME = self.fdout_fn
        executor.MAX_RUNNING_TIME = 4
        executor.MIN_RUNNING_TIME = 2
        executor.DEFAULT_TAIL_LINES = 1
        executor.PROCESS_POLL_INTERVAL = 2
        executor.MAX_RETRY = 3
        deploy_report = executor.run_cmd(cmd=fdout_fn)
        self.assertTrue(ping_server.called)
        self.assertEqual(deploy_report.status_code, AgentStatus.ABORTED_BY_SERVER)
        os.remove(fdout_fn)

    def test_run_command(self):
        cmd = ['echo', 'hello world']
        self.executor.MAX_RUNNING_TIME = 5
        self.executor.MAX_RETRY = 3
        self.executor.PROCESS_POLL_INTERVAL = 2
        self.executor.MIN_RUNNING_TIME = 2
        self.executor.BACK_OFF = 2
        self.executor.MAX_SLEEP_INTERVAL = 60
        self.executor.MAX_TAIL_BYTES = 10240
        self.executor.LOG_FILENAME = self.fdout_fn
        deploy_report = self.executor.run_cmd(cmd=cmd)
        self.assertEqual(deploy_report.status_code, AgentStatus.SUCCEEDED)

    def test_run_command_with_big_output(self):
        cmd = ['seq', '1000000']
        self.executor.MIN_RUNNING_TIME = 2
        deploy_report = self.executor.run_cmd(cmd=cmd)
        self.assertEqual(deploy_report.status_code, AgentStatus.SUCCEEDED)
        self.assertIsNotNone(deploy_report.output_msg)

    def test_run_command_with_max_retry(self):
        cmd = ['ls', '-ltr', '/abc']
        ping_server = mock.Mock(return_value=False)
        executor = Executor(callback=ping_server)
        executor.LOG_FILENAME = self.fdout_fn
        executor.MAX_RUNNING_TIME = 5
        executor.MIN_RUNNING_TIME = 2
        executor.MAX_RETRY = 3
        executor.DEFAULT_TAIL_LINES = 1
        executor.PROCESS_POLL_INTERVAL = 2
        executor.BACK_OFF = 2
        executor.MAX_SLEEP_INTERVAL = 60
        executor.MAX_TAIL_BYTES = 10240
        deploy_report = executor.run_cmd(cmd=cmd)
        self.assertEqual(deploy_report.status_code, AgentStatus.TOO_MANY_RETRY)
        msg = subprocess.check_output(['tail', '-1', self.fdout_fn])
        self.assertEqual(msg, 'ls: /abc: No such file or directory\n')
        self.assertEqual(deploy_report.retry_times, 3)
        self.assertEqual(deploy_report.output_msg, 'ls: /abc: No such file or directory')

    def test_run_command_with_timeout(self):
        cmd = ['ls', '-ltr', '/abc']
        ping_server = mock.Mock(return_value=True)
        executor = Executor(callback=ping_server)
        executor.LOG_FILENAME = self.fdout_fn
        executor.MAX_RUNNING_TIME = 4
        executor.MIN_RUNNING_TIME = 2
        executor.DEFAULT_TAIL_LINES = 1
        executor.MAX_RETRY = 3
        executor.PROCESS_POLL_INTERVAL = 2
        executor.MAX_TAIL_BYTES = 10240
        deploy_report = executor.run_cmd(cmd=cmd)
        self.assertEqual(deploy_report.status_code, AgentStatus.ABORTED_BY_SERVER)
        msg = subprocess.check_output(['tail', '-1', self.fdout_fn])
        self.assertEqual(msg, 'ls: /abc: No such file or directory\n')
        self.assertEqual(deploy_report.output_msg, 'ls: /abc: No such file or directory')

    def test_run_command_with_timeout_error(self):
        cmd = ['sleep', '20']
        ping_server = mock.Mock(return_value=False)
        executor = Executor(callback=ping_server)
        executor.LOG_FILENAME = self.fdout_fn
        executor.MAX_RUNNING_TIME = 4
        executor.MIN_RUNNING_TIME = 2
        executor.DEFAULT_TAIL_LINES = 1
        executor.MAX_RETRY = 3
        executor.PROCESS_POLL_INTERVAL = 2
        executor.BACK_OFF = 2
        executor.MAX_SLEEP_INTERVAL = 60
        executor.MAX_TAIL_BYTES = 10240
        deploy_report = executor.run_cmd(cmd=cmd)
        self.assertTrue(ping_server.called)
        self.assertEqual(deploy_report.status_code, AgentStatus.SCRIPT_TIMEOUT)


if __name__ == '__main__':
    unittest.main()