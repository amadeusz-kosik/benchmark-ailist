.PHONY: clean venv

clean:
	rm -fr ./.venv

setup:
	python3 -m venv .venv

run: .venv/bin/activate
	source ./.venv/bin/activate; jupyter lab

.venv/bin/activate: requirements.txt
	python3 -m venv .venv
	source ./.venv/bin/activate; pip install -Ur requirements.txt