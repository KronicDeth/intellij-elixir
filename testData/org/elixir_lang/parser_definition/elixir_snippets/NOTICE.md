# Snippets from Elixir's tests

`snippets.jsonl` holds, one JSON object per line, the source strings that Elixir's own parser, tokenizer,
formatter and normalizer tests hand to the parser. They come from https://github.com/elixir-lang/elixir, which is
licensed under the Apache License, Version 2.0, the licence of this repository (`LICENSE.md`).

It was changed as follows: `generate.exs` extracted each snippet from a string literal, sigil or charlist in
a test file, interpreted the escapes of `~s` and `~c` sigils, and kept one copy of each snippet across the
releases below. Each line records the snippet's `hash`, the `releases` whose tests contain it, and its
`origin`: the release, commit, file, line and helper where it first appears.

## Sources

| Release | Commit |
|---|---|
| 1.11.4 | [`308255bda81e7f76f9bec838cef033e8e869981b`](https://github.com/elixir-lang/elixir/tree/308255bda81e7f76f9bec838cef033e8e869981b) |
| 1.12.3 | [`74bfab8ee271e53d24cb0012b5db1e2a931e0470`](https://github.com/elixir-lang/elixir/tree/74bfab8ee271e53d24cb0012b5db1e2a931e0470) |
| 1.13.4 | [`7e4fbe657dbf9c3e19e3d2bd6c17cc6d724b4710`](https://github.com/elixir-lang/elixir/tree/7e4fbe657dbf9c3e19e3d2bd6c17cc6d724b4710) |
| 1.14.5 | [`81d6007410b3ae43e279cfb689bd03ffc5f06830`](https://github.com/elixir-lang/elixir/tree/81d6007410b3ae43e279cfb689bd03ffc5f06830) |
| 1.15.8 | [`29fdd09c0fcb5f029d43591665de35491f7378dd`](https://github.com/elixir-lang/elixir/tree/29fdd09c0fcb5f029d43591665de35491f7378dd) |
| 1.16.3 | [`327063cc8485692f2b902553fb599a2bef5a0f8f`](https://github.com/elixir-lang/elixir/tree/327063cc8485692f2b902553fb599a2bef5a0f8f) |
| 1.17.3 | [`78f63d08313677a680868685701ae79a2459dcc1`](https://github.com/elixir-lang/elixir/tree/78f63d08313677a680868685701ae79a2459dcc1) |
| 1.18.4 | [`7b20c281d521aa7aa2ad2baa1e9ae6c579d79d0c`](https://github.com/elixir-lang/elixir/tree/7b20c281d521aa7aa2ad2baa1e9ae6c579d79d0c) |
| 1.19.5 | [`d33fd8e413fe98e0e54dde7ef94fadb272b1ef67`](https://github.com/elixir-lang/elixir/tree/d33fd8e413fe98e0e54dde7ef94fadb272b1ef67) |
| 1.20.4 | [`759443e724f55bf58e71c0603644e99058918d52`](https://github.com/elixir-lang/elixir/tree/759443e724f55bf58e71c0603644e99058918d52) |

## Copyright and licence notices of the source files

`lib/elixir/test/elixir/code_formatter/calls_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/comments_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/containers_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/general_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/integration_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/literals_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_formatter/migration_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team

`lib/elixir/test/elixir/code_formatter/operators_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_normalizer/formatted_ast_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/code_normalizer/quoted_ast_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team

`lib/elixir/test/elixir/code_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/kernel/diagnostics_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team

`lib/elixir/test/elixir/kernel/errors_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

`lib/elixir/test/elixir/kernel/parser_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team

`lib/elixir/test/elixir/kernel/string_tokenizer_test.exs`, 1.19.5, 1.20.4:

    # SPDX-License-Identifier: Apache-2.0
    # SPDX-FileCopyrightText: 2021 The Elixir Team
    # SPDX-FileCopyrightText: 2012 Plataformatec

## NOTICE, 1.11.4, 1.12.3

    LEGAL NOTICE INFORMATION
    ------------------------

    All the files in this distribution are copyright to the terms below.

    == lib/elixir/src/elixir_parser.erl (generated by build scripts)

    Copyright Ericsson AB 1996-2015

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    == All other files

    Copyright 2012 Plataformatec

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## NOTICE, 1.13.4, 1.14.5, 1.15.8, 1.16.3, 1.17.3

    LEGAL NOTICE INFORMATION
    ------------------------

    All the files in this distribution are copyright to the terms below.

    == lib/elixir/src/elixir_parser.erl (generated by build scripts)

    Copyright Ericsson AB 1996-2015

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    == All other files

    Copyright 2012 Plataformatec
    Copyright 2021 The Elixir Team

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## NOTICE, 1.18.4

    LEGAL NOTICE INFORMATION
    ------------------------

    All the files in this distribution are copyright to the terms below.

    == lib/elixir/src/elixir_json.erl
    == lib/elixir/src/elixir_parser.erl (generated by build scripts)

    Copyright Ericsson AB 1996-2024

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    == All other files

    Copyright 2012 Plataformatec
    Copyright 2021 The Elixir Team

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
